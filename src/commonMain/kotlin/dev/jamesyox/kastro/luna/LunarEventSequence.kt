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

import dev.jamesyox.kastro.sol.SolarEvent
import dev.jamesyox.kastro.util.mergeWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * [Sequence] representing all [LunarEvent]s supported by Kastro. The sequence will be ordered by [SolarEvent.time]
 * either advancing forwards or backwards in time depending on the `reverse` parameter.
 *
 * As implied by being a [Sequence] values will be lazily calculated so setting a large or even infinite
 * limit is acceptable.
 *
 * @param start Time representing the beginning of the sequence
 * @param latitude Latitude of the observer in degrees
 * @param longitude Longitude of the observer in degrees
 * @param limit Point at which the sequence will end. If you wish for an infinite sequence simply put
 * [Duration.INFINITE] for limit. Calculations are done lazily so requesting an infinite sequence is not harmful.
 * Limit only exists to allow certain logical checks such as if you wanted to know if there will be any event in the
 * next hour you could use limit and then check if the resulting sequence is empty. Defaults to 365 days.
 * @param requestedLunarEvents A [List] of [LunarEvent.LunarEventType]s to calculate event times for.
 * @param reverse Whether the sequence should advance in reverse chronological order. By default, this is false
 */
public class LunarEventSequence(
    start: Instant,
    latitude: Double,
    longitude: Double,
    limit: Duration = 365.days,
    requestedLunarEvents: List<LunarEvent.LunarEventType> = LunarEvent.all,
    private val reverse: Boolean = false
) : Sequence<LunarEvent> {

    /**
     * [Sequence] representing all [LunarEvent]s supported by Kastro. The sequence will be ordered by [SolarEvent.time]
     * either advancing forwards or backwards in time depending on the `reverse` parameter.
     *
     * As implied by being a [Sequence] values will be lazily calculated so setting a large or even infinite
     * limit is acceptable.
     *
     * @param start Time representing the beginning of the sequence
     * @param location
     * Pair where the first [Double] represents the latitude in degrees and the second represents longitude in degrees.
     * @param limit Point at which the sequence will end. If you wish for an infinite sequence simply put
     * [Duration.INFINITE] for limit. Calculations are done lazily so requesting an infinite sequence is not harmful.
     * Limit only exists to allow certain logical checks such as if you wanted to know if there will be any event in the
     * next hour you could use limit and then check if the resulting sequence is empty. Defaults to 365 days.
     * @param requestedLunarEvents A [List] of [LunarEvent.LunarEventType]s to calculate event times for.
     * @param reverse Whether the sequence should advance in reverse chronological order. By default, this is false
     * resulting in a Sequence that advances chronologically in time.
     */
    public constructor(
        start: Instant,
        location: Pair<Double, Double>,
        limit: Duration = 365.days,
        requestedLunarEvents: List<LunarEvent.LunarEventType> = LunarEvent.all,
        reverse: Boolean = false,
    ) : this(start, location.first, location.second, limit, requestedLunarEvents, reverse)

    private val lunarPhaseSequence = LunarPhaseSequence(
        start = start,
        limit = limit,
        requestedLunarPhases = requestedLunarEvents.filterIsInstance<LunarPhase.Primary>(),
        reverse = reverse
    )

    private val lunarHorizonEventSequence = LunarHorizonEventSequence(
        start = start,
        latitude = latitude,
        longitude = longitude,
        limit = limit,
        requestedHorizonEvents = requestedLunarEvents.filterIsInstance<LunarEvent.HorizonEvent.HorizonEventType>(),
        reverse = reverse
    )

    override fun iterator(): Iterator<LunarEvent> {
        return lunarPhaseSequence.mergeWith(lunarHorizonEventSequence, reverse).iterator()
    }
}
