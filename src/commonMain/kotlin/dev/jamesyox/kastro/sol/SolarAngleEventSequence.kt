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
import dev.jamesyox.kastro.util.Sol
import dev.jamesyox.kastro.util.instant
import dev.jamesyox.kastro.util.julianDate
import dev.jamesyox.kastro.util.radians
import kotlinx.datetime.Instant
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

internal class SolarAngleEventSequence(
    private val start: Instant,
    private val latitude: Latitude,
    private val longitude: Longitude,
    private val limit: Duration,
    private val requestedAngleEvents: List<SolarEventType.Angle>,
    // private val height: Double = 0.0 // TODO [Alpha]: See comment below
) : Sequence<SolarEvent> {

    private val limitTime = start + limit
    private val chunkTime = 24.hours

    private fun getLocalLimit(start: Instant): Duration {
        return if ((start + chunkTime) < limitTime) chunkTime else limitTime - start
    }

    override fun iterator(): Iterator<SolarEvent> {
        var currentTime = start
        return generateSequence {
            if (currentTime <= start + limit) {
                calculateNextSolarEvents(
                    localStart = currentTime,
                    localLimit = getLocalLimit(currentTime)
                )
            } else {
                null
            }.also {
                currentTime += (chunkTime + 1.seconds)
            }
        }.flatten().iterator()
    }

    private fun calculateNextSolarEvents(localStart: Instant, localLimit: Duration): Sequence<SolarEvent> {
        return requestedAngleEvents
            .asSequence()
            .mapNotNull { angle ->
                calculateNextSolarAngleEvent(
                    localStart = localStart,
                    // TODO [Alpha]: Height offset calculation is incorrect. This is safe for now but eventually want to
                    //      add height back to public API.
                    height = 0.0,
                    angle = angle,
                    localLimit = localLimit
                )
            }.sortedBy { it.time }
    }

    // This function is quite complex. There's potentially ways to simplify these algorithms or break them
    // up.
    @Suppress("ComplexCondition", "NestedBlockDepth", "CyclomaticComplexMethod", "ReturnCount")
    private fun calculateNextSolarAngleEvent(
        localStart: Instant,
        angle: SolarEventType.Angle,
        height: Double,
        localLimit: Duration,
    ): SolarEvent? {
        val julianDate = localStart.julianDate

        /**
         * Computes the sun height at the given date and position.
         *
         * @param jd [JulianDate] to use
         * @return height, in radians
         */
        fun correctedSunHeight(jd: JulianDate): Double {
            val sunPos = when (angle) {
                is SolarEventType.TopocentricAngle -> jd.calculateSolarState(
                    latitude = latitude,
                    longitude = longitude,
                    height = height,
                )
                else -> jd.calculateSolarState(latitude = latitude, longitude = longitude, height = height)
            }
            val angleRad = angle.angle.radians
            val angleModifier = when (angle) {
                is SolarEventType.TopocentricAngle -> {
                    val angularPositionRad = angle.angularPosition * Sol.angularRadius(sunPos.distance)
                    sunPos.parallaxRad - angularPositionRad - ExtendedMath.apparentRefraction(angleRad)
                }
                else -> 0.0
            }
            return sunPos.trueAltitudeRad - (angleRad + angleModifier)
        }
        var hour = 0
        val limitHours = localLimit.inWholeMilliseconds.toDouble() /
            1.hours.inWholeMilliseconds.toDouble()
        val maxHours = ceil(limitHours).toInt()
        var yMinus = correctedSunHeight(julianDate.atHour(hour - 1.0))
        var y0 = correctedSunHeight(julianDate.atHour(hour.toDouble()))
        var yPlus = correctedSunHeight(julianDate.atHour(hour + 1.0))
        while (hour <= maxHours) {
            val qi = QuadraticInterpolation.of(yMinus, y0, yPlus)
            val ye = qi.ye
            if (qi.numberOfRoots == 1) {
                val rt = qi.root1 + hour
                if (yMinus < 0.0) {
                    if (angle is SolarEventType.Angle.Dawn && rt >= 0.0 && rt < limitHours) {
                        return angle.eventAt(julianDate.atHour(rt).instant)
                    }
                } else {
                    if (angle is SolarEventType.Angle.Dusk && rt >= 0.0 && rt < limitHours) {
                        return angle.eventAt(julianDate.atHour(rt).instant)
                    }
                }
            } else if (qi.numberOfRoots == 2) {
                (hour + if (ye < 0.0) qi.root2 else qi.root1).let { rt ->
                    if (angle is SolarEventType.Angle.Dawn && rt >= 0.0 && rt < limitHours) {
                        return angle.eventAt(julianDate.atHour(rt).instant)
                    }
                }
                (hour + if (ye < 0.0) qi.root1 else qi.root2).let { rt ->
                    if (angle is SolarEventType.Angle.Dusk && rt >= 0.0 && rt < limitHours) {
                        return angle.eventAt(julianDate.atHour(rt).instant)
                    }
                }
            }
            hour++
            yMinus = y0
            y0 = yPlus
            yPlus = correctedSunHeight(julianDate.atHour(hour + 1.0))
        }
        return null
    }
}
