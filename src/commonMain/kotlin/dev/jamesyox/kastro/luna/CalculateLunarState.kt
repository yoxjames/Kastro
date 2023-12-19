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

import kotlinx.datetime.Instant

/**
 * For a given time calculate the [LunarState] for a relative position. [LunarState] is a class containing the
 * results of multiple calculations. Invoking this function provides maximum information about the moon for a given
 * time/location. Therefore, this runs calculations requiring location and not requiring location, so you must have
 * location to invoke.
 *
 * @receiver The time you wish to calculate [LunarState] for
 * @param latitude The longitude in degrees
 * @param longitude The longitude in degrees
 */
public fun Instant.calculateLunarState(
    latitude: Double,
    longitude: Double
): LunarState {
    return LunarState(
        position = calculateLunarPosition(latitude = latitude, longitude = longitude),
        illumination = calculateLunarIllumination()
    )
}

/**
 * For a given time calculate the [LunarState] for a relative position. [LunarState] is a class containing the results
 * of multiple calculations. Invoking this function provides maximum information about the moon for a given
 * time/location. Therefore, this runs calculations requiring location and not requiring location, so you must have
 * location to invoke.
 *
 * @receiver The time you wish to calculate [LunarState] for
 * @param location A pair where the first value represents latitude and the second longitude both in degrees.
 */
public fun Instant.calculateLunarState(location: Pair<Double, Double>): LunarState {
    return calculateLunarState(latitude = location.first, longitude = location.second)
}
