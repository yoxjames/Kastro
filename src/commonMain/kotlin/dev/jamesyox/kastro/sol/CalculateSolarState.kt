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

import dev.jamesyox.kastro.util.ExtendedMath.parallax
import dev.jamesyox.kastro.util.ExtendedMath.refraction
import dev.jamesyox.kastro.util.JulianDate
import dev.jamesyox.kastro.util.Latitude
import dev.jamesyox.kastro.util.Longitude
import dev.jamesyox.kastro.util.Sol.positionHorizontal
import dev.jamesyox.kastro.util.degrees
import dev.jamesyox.kastro.util.julianDate
import dev.jamesyox.kastro.util.latitude
import dev.jamesyox.kastro.util.longitude
import kotlinx.datetime.Instant

/**
 * Calculates the [SolarState] for a given [Instant] in time
 *
 *
 * @receiver Instant in time to calculate the [SolarState] for
 * @param location
 * Pair where the [Pair.first] value is the latitude in degrees and the [Pair.second] value is the
 * longitude in degrees
 * @param height The height of the observer in meters
 */
public fun Instant.calculateSolarState(
    location: Pair<Double, Double>,
    height: Double = 0.0,
): SolarState {
    return calculateSolarState(location.first, location.second, height)
}

/**
 * Calculates the [SolarState] for a given [Instant] in time
 *
 *
 * @receiver Instant in time to calculate the [SolarState] for
 * @param latitude The latitude in degrees
 * @param longitude The longitude in degrees
 * @param height The height of the observer in meters
 */
public fun Instant.calculateSolarState(
    latitude: Double,
    longitude: Double,
    height: Double = 0.0,
): SolarState {
    return julianDate.calculateSolarState(
        latitude = latitude.latitude,
        longitude = longitude.longitude,
        height = height.coerceAtLeast(0.0),
    )
}

internal fun JulianDate.calculateSolarState(
    latitude: Latitude,
    longitude: Longitude,
    height: Double,
): SolarState {
    val jd = this
    val pos = positionHorizontal(jd, latitude, longitude)
    val hRef = refraction(pos.theta)
    val distance = pos.r
    return SolarState(
        azimuth = (pos.phi.degrees + 180.0) % 360,
        atmosphericRefractionRad = hRef,
        distance = distance,
        trueAltitudeRad = pos.theta,
        parallaxRad = parallax(height = height, distance = distance)
    )
}
