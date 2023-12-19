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
package dev.jamesyox.kastro.util

import dev.jamesyox.kastro.util.ExtendedMath.PI2
import dev.jamesyox.kastro.util.ExtendedMath.equatorialToEcliptical
import dev.jamesyox.kastro.util.ExtendedMath.equatorialToHorizontal
import dev.jamesyox.kastro.util.ExtendedMath.frac
import dev.jamesyox.kastro.util.Vector.Companion.ofPolar
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

/**
 * Calculations and constants Sol.
 *
 * @see "Astronomy on the Personal Computer, 4th edition
 *
 */
internal object Sol {
    private const val SUN_DISTANCE = 149598000.0
    private const val SUN_MEAN_RADIUS = 695700.0

    /**
     * Calculates the equatorial position of sol.
     *
     * @param date
     * [JulianDate] to be used
     * @return [Vector] containing the sun position
     */
    fun positionEquatorial(date: JulianDate): Vector {
        val t = date.julianCentury
        val m: Double = PI2 * frac(0.993133 + 99.997361 * t)
        val l: Double = PI2 * frac(
            0.7859453 + m / PI2 +
                (6893.0 * sin(m) + 72.0 * sin(2.0 * m) + 6191.2 * t) / 1296.0e3
        )
        val d = (
            SUN_DISTANCE
                * (1 - 0.016718 * cos(date.trueAnomaly))
            )
        return ofPolar(l, 0.0, d)
    }

    /**
     * Calculates the geocentric position of the sun.
     *
     * @param date
     * [JulianDate] to be used
     * @return [Vector] containing the sun position
     */
    fun position(date: JulianDate): Vector {
        val rotateMatrix = equatorialToEcliptical(date).transpose()
        return rotateMatrix.multiply(positionEquatorial(date))
    }

    /**
     * Calculates the horizontal position of the sun.
     *
     * @param date
     * [JulianDate] to be used
     * @param lat
     * Latitude, in radians
     * @param lng
     * Longitude, in radians
     * @return [Vector] of horizontal sun position
     */
    fun positionHorizontal(date: JulianDate, lat: Latitude, lng: Longitude): Vector {
        val mc = position(date)
        val h = date.greenwichMeanSiderealTime + lng.radians - mc.phi
        return equatorialToHorizontal(h, mc.theta, mc.r, lat)
    }

    /**
     * Returns the angular radius of the sun.
     *
     * @param distance
     * Distance of the sun, in kilometers.
     * @return Angular radius of the sun, in radians.
     * @see [Wikipedia: Angular
     * Diameter](https://en.wikipedia.org/wiki/Angular_diameter)
     */
    fun angularRadius(distance: Double): Double {
        return asin(SUN_MEAN_RADIUS / distance)
    }
}
