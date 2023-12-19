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
import dev.jamesyox.kastro.util.JulianDate
import dev.jamesyox.kastro.util.Latitude
import dev.jamesyox.kastro.util.Longitude
import dev.jamesyox.kastro.util.QuadraticInterpolation
import dev.jamesyox.kastro.util.instant
import dev.jamesyox.kastro.util.julianDate
import kotlinx.datetime.Instant
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

internal class NoonAndNadirSequence(
    private val start: Instant,
    private val latitude: Latitude,
    private val longitude: Longitude,
    private val limit: Duration,
    private val requestedCulminationEvents: List<SolarEventType.Culmination>
) : Sequence<SolarEvent> {
    override fun iterator(): Iterator<SolarEvent> {
        // Skip everything if we requested no culmination events
        if (requestedCulminationEvents.isEmpty()) return emptySequence<SolarEvent>().iterator()

        return generateSequence(calculateNextNoonAndNadir(start.julianDate)) {
            it.lastOrNull()?.time?.let { lastTime ->
                if (lastTime <= start + limit) calculateNextNoonAndNadir((lastTime + 1.seconds).julianDate) else null
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
    @Suppress("ComplexCondition", "NestedBlockDepth", "CyclomaticComplexMethod")
    private fun calculateNextNoonAndNadir(jd: JulianDate): Sequence<SolarEvent> {
        var hour = 0
        var noon: Double? = null
        var nadir: Double? = null
        val limitHours = limit.inWholeMilliseconds.toDouble() / 1.hours.inWholeMilliseconds
        val maxHours = ceil(limitHours).toInt()
        var yMinus = calculateSolHeightRad(jd.atHour(hour - 1.0))
        var y0 = calculateSolHeightRad(jd.atHour(hour.toDouble()))
        var yPlus = calculateSolHeightRad(jd.atHour(hour + 1.0))
        while (hour <= maxHours) {
            val qi = QuadraticInterpolation.of(yMinus, y0, yPlus)
            val xeAbs = abs(qi.xe)
            if (xeAbs <= 1.0) {
                val xeHour = qi.xe + hour
                if (xeHour >= 0.0) {
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
            hour++
            yMinus = y0
            y0 = yPlus
            yPlus = calculateSolHeightRad(jd.atHour(hour + 1.0))
        }
        if (noon != null && isNoonRequested) {
            noon = ExtendedMath.readjustMax(time = noon, frame = 2.0, depth = 14) {
                calculateSolHeightRad(jd.atHour(it))
            }
            if (noon < 0.0 || noon >= limitHours) {
                noon = null
            }
        }
        if (nadir != null && isNadirRequested) {
            nadir = ExtendedMath.readjustMin(time = nadir, frame = 2.0, depth = 14) {
                calculateSolHeightRad(jd.atHour(it))
            }
            if (nadir < 0.0 || nadir >= limitHours) {
                nadir = null
            }
        }
        return sequence {
            noon?.also { yield(SolarEvent.Noon(jd.atHour(it).instant)) }
            nadir?.also { yield(SolarEvent.Nadir(jd.atHour(it).instant)) }
        }.sortedBy { it.time }
    }
}
