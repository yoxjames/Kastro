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

import dev.jamesyox.kastro.util.latitude
import dev.jamesyox.kastro.util.longitude
import dev.jamesyox.kastro.util.mergeWith
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * [Sequence] representing all [SolarEvent]s supported by Kastro. The sequence will be ordered by [SolarEvent.time]
 * with times farthest in the future being later in the [Sequence]. As implied by being a [Sequence] values will be
 * lazily calculated so setting a large or even infinite limit is fine.
 *
 * @param start Time representing the beginning of the sequence
 * @param latitude Latitude of the observer in degrees
 * @param longitude Longitude of the observer in degrees
 * @param limit Point at which the sequence will end. If you wish for an infinite sequence simply put
 * [Duration.INFINITE] for limit. Calculations are done lazily so requesting an infinite sequence is not harmful.
 * Limit only exists to allow certain logical checks such as if you wanted to know if there will be any event in the
 * next hour you could use limit and then check if the resulting sequence is empty. Defaults to 365 days.
 * @param requestedSolarEvents A [List] of [SolarEventType]s to calculate event times for. Requesting only events you
 * are interested in will save computation resources. By default, only includes [SolarEventType.simple] events.
 */
public class SolarEventSequence(
    start: Instant,
    latitude: Double,
    longitude: Double,
    limit: Duration = 365.days,
    requestedSolarEvents: List<SolarEventType> = SolarEventType.simple
) : Sequence<SolarEvent> {

    /**
     * Alternative constructor with location as a [Pair]. Provided as a convenience. See primary constructor for more
     * information
     *
     * @param start Time representing the beginning of the sequence
     * @param location
     * Pair where the first [Double] represents the latitude in degrees and the second represents longitude in degrees.
     * @param limit The limit or outer bound of the Sequence see primary constructor for more information.
     * @param requestedSolarEvents A [List] of [SolarEventType]s to calculate event times for. Requesting only events
     * you are interested in will save computation resources. By default, only includes [SolarEventType.simple] events.
     *
     * @see SolarEventSequence
     */
    public constructor(
        start: Instant,
        location: Pair<Double, Double>,
        limit: Duration = 365.days,
        requestedSolarEvents: List<SolarEventType> = SolarEventType.simple
    ) : this(start, location.first, location.second, limit, requestedSolarEvents)

    private val noonAndNadirSequence = NoonAndNadirSequence(
        start = start,
        latitude = latitude.latitude,
        longitude = longitude.longitude,
        limit = limit,
        requestedCulminationEvents = requestedSolarEvents.filterIsInstance<SolarEventType.Culmination>()
    )

    private val solarAngleEventSequence = SolarAngleEventSequence(
        start = start,
        latitude = latitude.latitude,
        longitude = longitude.longitude,
        limit = limit,
        requestedAngleEvents = requestedSolarEvents.filterIsInstance<SolarEventType.Angle>()
    )

    override fun iterator(): Iterator<SolarEvent> {
        return solarAngleEventSequence.mergeWith(noonAndNadirSequence).iterator()
    }
}
