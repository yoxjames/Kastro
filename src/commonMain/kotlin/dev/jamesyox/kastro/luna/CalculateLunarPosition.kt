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

import dev.jamesyox.kastro.util.ExtendedMath.equatorialToHorizontal
import dev.jamesyox.kastro.util.ExtendedMath.refraction
import dev.jamesyox.kastro.util.JulianDate
import dev.jamesyox.kastro.util.Latitude
import dev.jamesyox.kastro.util.Longitude
import dev.jamesyox.kastro.util.Moon
import dev.jamesyox.kastro.util.degrees
import dev.jamesyox.kastro.util.greenwichMeanSiderealTime
import dev.jamesyox.kastro.util.julianDate
import dev.jamesyox.kastro.util.latitude
import dev.jamesyox.kastro.util.longitude
import dev.jamesyox.kastro.util.radians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.time.Instant

/**
 * Calculates the [LunarPosition] for a given instant in time.
 *
 * @receiver The time you wish to calculate [LunarPosition] for
 * @param latitude The latitude in degrees
 * @param longitude The longitude in degrees
 * @return The [LunarPosition]
 * @see LunarPosition
 */
public fun Instant.calculateLunarPosition(latitude: Double, longitude: Double): LunarPosition {
    return calculateLunarPosition(
        julianDate = julianDate,
        latitude = latitude.latitude,
        longitude = longitude.longitude
    )
}

/**
 * Calculates the [LunarPosition] for a given instant in time.
 *
 * @receiver The time you wish to calculate [LunarPosition] for
 * @param location A pair where the first value represents latitude and the second longitude both in degrees.
 * @return The [LunarPosition]
 * @see LunarPosition
 */
public fun Instant.calculateLunarPosition(location: Pair<Double, Double>): LunarPosition {
    return this@calculateLunarPosition.calculateLunarPosition(latitude = location.first, longitude = location.second)
}

private fun calculateLunarPosition(
    julianDate: JulianDate,
    latitude: Latitude,
    longitude: Longitude,
): LunarPosition {
    val mc = Moon.position(julianDate)
    val h = julianDate.greenwichMeanSiderealTime + longitude.radians - mc.phi
    val horizontal = equatorialToHorizontal(h, mc.theta, mc.r, latitude)
    val hRef = refraction(horizontal.theta)
    val pa = atan2(sin(h), tan(latitude.radians) * cos(mc.theta) - sin(mc.theta) * cos(h))
    return LunarPosition(
        azimuth = (horizontal.phi.degrees + 180.0) % 360.0,
        altitude = (horizontal.theta + hRef).degrees,
        distance = julianDate.calculateLunarDistance(),
        parallacticAngleRad = pa
    )
}
