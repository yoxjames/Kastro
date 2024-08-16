/*
 * Copyright (C) 2023 James Yox
 *   http://www.jamesyox.dev
 * Copyright (C) 2017 Richard "Shred" Körber
 *    http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package dev.jamesyox.kastro.sol

import dev.jamesyox.kastro.util.ExtendedMath
import dev.jamesyox.kastro.util.ExtendedMath.readjustMax
import dev.jamesyox.kastro.util.JulianDate
import dev.jamesyox.kastro.util.Latitude
import dev.jamesyox.kastro.util.Longitude
import dev.jamesyox.kastro.util.QuadraticInterpolation
import dev.jamesyox.kastro.util.instant
import dev.jamesyox.kastro.util.isOutsideLimit
import dev.jamesyox.kastro.util.isWithinLimit
import dev.jamesyox.kastro.util.julianDate
import dev.jamesyox.kastro.util.sortedByReversible
import kotlinx.datetime.Instant
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class NoonAndNadirSequence(
    private val start: Instant,
    private val latitude: Latitude,
    private val longitude: Longitude,
    private val limit: Duration,
    private val requestedCulminationEvents: List<SolarEventType.Culmination>,
    private val reverse: Boolean
) : Sequence<SolarEvent> {
    private val limitTime = if (reverse) start - limit else start + limit

    override fun iterator(): Iterator<SolarEvent> {
        // Skip everything if we requested no culmination events
        if (requestedCulminationEvents.isEmpty()) return emptySequence<SolarEvent>().iterator()

        return generateSequence(calculateNextNoonAndNadir(start, reverse)) {
            it.lastOrNull()?.time?.let { lastTime ->
                // Jumping ahead by an hour is fine since Noon and Nadir must be pretty far apart.
                // Probably could jump farther.

                // Determine whether to go back or forward 1 hours depending on reverse param
                val advanceBy = if (reverse) (-1).hours else 1.hours
                val withinThreshold = if (reverse) lastTime >= start - limit else lastTime <= start + limit
                if (withinThreshold) calculateNextNoonAndNadir((lastTime + advanceBy), reverse) else null
            }
        }.flatten().iterator()
    }

    private val isNadirRequested = requestedCulminationEvents.contains(SolarEvent.Nadir)
    private val isNoonRequested = requestedCulminationEvents.contains(SolarEvent.Noon)

    private fun calculateSolHeightRad(jd: JulianDate): Double {
        return jd.calculateSolarState(
            latitude = latitude,
            longitude = longitude,
            height = 0.0
        ).trueAltitudeRad
    }

    // This function is quite complex. There's potentially ways to simplify these algorithms or break them
    // up.
    @Suppress("ComplexCondition", "NestedBlockDepth", "CyclomaticComplexMethod", "LongMethod")
    private fun calculateNextNoonAndNadir(localStart: Instant, reverse: Boolean): Sequence<SolarEvent> {
        val julianDate = localStart.julianDate
        var hour = 0
        var noon: Double? = null
        var nadir: Double? = null
        val limitHours = (limitTime - localStart).inWholeMilliseconds.toDouble() /
            1.hours.inWholeMilliseconds.toDouble()
        val hourLimit = if (reverse) floor(limitHours).toInt() else ceil(limitHours).toInt()
        var yMinus = calculateSolHeightRad(julianDate.atHour(hour - 1.0))
        var y0 = calculateSolHeightRad(julianDate.atHour(hour.toDouble()))
        var yPlus = calculateSolHeightRad(julianDate.atHour(hour + 1.0))
        while (hour.isWithinLimit(reverse = reverse, limit = hourLimit)) {
            val qi = QuadraticInterpolation.of(yMinus, y0, yPlus)
            val xeAbs = abs(qi.xe)
            if (xeAbs <= 1.0) {
                val xeHour = qi.xe + hour
                if (if (reverse) xeHour <= 0.0 else xeHour >= 0.0) {
                    if (qi.isMaximum) {
                        if (noon == null && isNoonRequested) {
                            noon = xeHour
                        }
                    } else {
                        if (nadir == null && isNadirRequested) {
                            nadir = xeHour
                        }
                    }
                }
            }
            if ((noon != null || !isNoonRequested) && (nadir != null || !isNadirRequested)) break
            when (reverse) {
                true -> {
                    hour--
                    yPlus = y0
                    y0 = yMinus
                    yMinus = calculateSolHeightRad(julianDate.atHour(hour - 1.0))
                }
                false -> {
                    hour++
                    yMinus = y0
                    y0 = yPlus
                    yPlus = calculateSolHeightRad(julianDate.atHour(hour + 1.0))
                }
            }
        }
        if (noon != null && isNoonRequested) {
            noon = readjustMax(time = noon, frame = 2.0, depth = 14) {
                calculateSolHeightRad(julianDate.atHour(it))
            }
            if (noon.isOutsideLimit(reverse, limitHours)) {
                noon = null
            }
        }
        if (nadir != null && isNadirRequested) {
            nadir = ExtendedMath.readjustMin(time = nadir, frame = 2.0, depth = 14) {
                calculateSolHeightRad(julianDate.atHour(it))
            }
            if (nadir.isOutsideLimit(reverse, limitHours)) {
                nadir = null
            }
        }
        return sequence {
            noon?.also { yield(SolarEvent.Noon(julianDate.atHour(it).instant)) }
            nadir?.also { yield(SolarEvent.Nadir(julianDate.atHour(it).instant)) }
        }.sortedByReversible(reverse) { it.time }
    }
}
