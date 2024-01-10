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
package dev.jamesyox.kastro.luna

import dev.jamesyox.kastro.util.ExtendedMath.apparentRefraction
import dev.jamesyox.kastro.util.ExtendedMath.parallax
import dev.jamesyox.kastro.util.JulianDate
import dev.jamesyox.kastro.util.Moon.angularRadius
import dev.jamesyox.kastro.util.Moon.positionHorizontal
import dev.jamesyox.kastro.util.QuadraticInterpolation
import dev.jamesyox.kastro.util.instant
import dev.jamesyox.kastro.util.julianDate
import dev.jamesyox.kastro.util.latitude
import dev.jamesyox.kastro.util.longitude
import kotlinx.datetime.Instant
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * [Sequence] representing all [LunarEvent.HorizonEvent]s supported by Kastro. The sequence will be ordered by
 * [LunarEvent.time] with times farthest in the future being later in the [Sequence]. As implied by being a [Sequence]
 * values will be lazily calculated so setting a large or even infinite limit is fine.
 *
 * @param start Time representing the beginning of the sequence
 * @param latitude Latitude of the observer in degrees
 * @param longitude Longitude of the observer in degrees
 * @param limit Point at which the sequence will end. If you wish for an infinite sequence simply put
 * [Duration.INFINITE] for limit. Calculations are done lazily so requesting an infinite sequence is not harmful.
 * Limit only exists to allow certain logical checks such as if you wanted to know if there will be any event in the
 * next hour you could use limit and then check if the resulting sequence is empty. Defaults to 365 days.
 * @param requestedHorizonEvents A [List] of [LunarEvent.HorizonEvent.HorizonEventType]s to calculate event times for.
 */
public class LunarHorizonEventSequence(
    private val start: Instant,
    latitude: Double,
    longitude: Double,
    private val limit: Duration = 365.days,
    private val requestedHorizonEvents: List<LunarEvent.HorizonEvent.HorizonEventType> = LunarEvent.HorizonEvent.all
) : Sequence<LunarEvent.HorizonEvent> {

    /**
     * Alternative constructor with location as a [Pair]. Provided as a convenience. See primary constructor for more
     * information
     *
     * @param start Time representing the beginning of the sequence
     * @param location
     * Pair where the first [Double] represents the latitude in degrees and the second represents longitude in degrees.
     * @param limit The limit or outer bound of the Sequence see primary constructor for more information.
     * @param requestedHorizonEvents A [List] of [LunarEvent.HorizonEvent.HorizonEventType]s to calculate event times
     * for.
     * @see LunarHorizonEventSequence
     */
    public constructor(
        start: Instant,
        location: Pair<Double, Double>,
        limit: Duration = 365.days,
        requestedHorizonEvents: List<LunarEvent.HorizonEvent.HorizonEventType> = LunarEvent.HorizonEvent.all
    ) : this(start, location.first, location.second, limit, requestedHorizonEvents)

    private val refraction = apparentRefraction(0.0) // TODO [ALPHA]
    private val lat = latitude.latitude
    private val lon = longitude.longitude
    private val height = 0.0 // TODO: Add back when we figure out what is wrong with calculation [ALPHA]

    public override fun iterator(): Iterator<LunarEvent.HorizonEvent> {
        if (requestedHorizonEvents.isEmpty()) {
            return emptySequence<LunarEvent.HorizonEvent>().iterator()
        }
        return generateSequence(execute()) {
            // Take the last of the sequence returned by execute and execute upon it if it is not null.
            execute(
                startHour = it.hour + 1,
                mode = if (it is InternalLunarHorizonEvent.RiseResult) SearchMode.SET else SearchMode.RISE
            )
        }.map {
            when (it) {
                is InternalLunarHorizonEvent.RiseResult -> LunarEvent.HorizonEvent.Moonrise(it.jd.atHour(it.rt).instant)
                is InternalLunarHorizonEvent.SetResult -> LunarEvent.HorizonEvent.Moonset(it.jd.atHour(it.rt).instant)
            }
        }.filter {
            // This could be integrated into the math rather than doing filtering after the fact.
            when (it) {
                is LunarEvent.HorizonEvent.Moonrise -> requestedHorizonEvents.contains(LunarEvent.HorizonEvent.Moonrise)
                is LunarEvent.HorizonEvent.Moonset -> requestedHorizonEvents.contains(LunarEvent.HorizonEvent.Moonset)
            }
        }.iterator()
    }

    private fun execute(): InternalLunarHorizonEvent? {
        val rise = execute(startHour = 0, mode = SearchMode.RISE)
        val set = execute(startHour = 0, mode = SearchMode.SET)

        return if (rise != null && set != null) {
            if (rise.hour < set.hour) rise else set
        } else {
            rise ?: set
        }
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
    private fun execute(
        startHour: Int,
        mode: SearchMode
    ): InternalLunarHorizonEvent? {
        /**
         * Computes the moon height at the given date and position.
         *
         * @param jd [JulianDate] to use
         * @return height, in radians
         */
        fun correctedLunarHeight(jd: JulianDate): Double {
            val pos = positionHorizontal(jd, lat, lon)
            val hc = (parallax(height, pos.r) - refraction - angularRadius(pos.r))
            return pos.theta - hc
        }
        val julianDate = start.julianDate
        var ye: Double
        var hour = startHour
        val limitHours = limit.inWholeMilliseconds / (60.0 * 60.0 * 1000.0)
        val maxHours = ceil(limitHours).toInt()
        var yMinus = correctedLunarHeight(julianDate.atHour(hour - 1.0))
        var y0 = correctedLunarHeight(julianDate.atHour(hour.toDouble()))
        var yPlus = correctedLunarHeight(julianDate.atHour(hour + 1.0))
        while (hour <= maxHours) {
            val qi = QuadraticInterpolation.of(yMinus, y0, yPlus)
            ye = qi.ye
            if (qi.numberOfRoots == 1) {
                val rt = qi.root1 + hour
                if (yMinus < 0.0) {
                    if (rt >= 0.0 && rt < limitHours && mode.checkRise) {
                        return InternalLunarHorizonEvent.RiseResult(jd = julianDate, rt = rt, hour = hour)
                    }
                } else {
                    if (rt >= 0.0 && rt < limitHours && mode.checkSet) {
                        return InternalLunarHorizonEvent.SetResult(jd = julianDate, rt = rt, hour = hour)
                    }
                }
            } else if (qi.numberOfRoots == 2) {
                (hour + if (ye < 0.0) qi.root2 else qi.root1).let { rt ->
                    if (rt >= 0.0 && rt < limitHours && mode.checkRise) {
                        return InternalLunarHorizonEvent.RiseResult(jd = julianDate, rt = rt, hour = hour)
                    }
                }

                (hour + if (ye < 0.0) qi.root1 else qi.root2).let { rt ->
                    if (rt >= 0.0 && rt < limitHours && mode.checkSet) {
                        return InternalLunarHorizonEvent.SetResult(jd = julianDate, rt = rt, hour = hour)
                    }
                }
            }
            hour++
            yMinus = y0
            y0 = yPlus
            yPlus = correctedLunarHeight(julianDate.atHour(hour + 1.0))
        }
        return null
    }
}

private enum class SearchMode {
    BOTH, RISE, SET
}

private val SearchMode.checkRise: Boolean get() = this == SearchMode.BOTH || this == SearchMode.RISE
private val SearchMode.checkSet: Boolean get() = this == SearchMode.BOTH || this == SearchMode.SET

/*
 * Note about all of this silliness. So originally I wanted to just pass around the julian date but was seeing results
 * sometimes be off slightly. So I passed the hour and feed the original julian date to the function. I feel like I
 * should just be able to pass the latest date going forward. Needs deeper analysis. Worth hunting down before alpha
 * ends.
 */
private sealed interface InternalLunarHorizonEvent {
    val jd: JulianDate
    val rt: Double
    val hour: Int
    data class RiseResult(
        override val jd: JulianDate,
        override val rt: Double,
        override val hour: Int,
    ) : InternalLunarHorizonEvent

    data class SetResult(
        override val jd: JulianDate,
        override val rt: Double,
        override val hour: Int,
    ) : InternalLunarHorizonEvent
}
