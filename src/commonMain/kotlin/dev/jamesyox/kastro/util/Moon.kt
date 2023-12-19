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

import dev.jamesyox.kastro.util.ExtendedMath.ARCS
import dev.jamesyox.kastro.util.ExtendedMath.PI2
import dev.jamesyox.kastro.util.ExtendedMath.equatorialToEcliptical
import dev.jamesyox.kastro.util.ExtendedMath.equatorialToHorizontal
import dev.jamesyox.kastro.util.ExtendedMath.frac
import dev.jamesyox.kastro.util.Vector.Companion.ofPolar
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

/**
 * Calculations and constants for the Moon.
 *
 * @see "Astronomy on the Personal Computer, 4th edition
 *
 */
internal object Moon {
    private const val MOON_MEAN_RADIUS = 1737.1

    /**
     * Calculates the equatorial position of the moon.
     *
     * @param date
     * [JulianDate] to be used
     * @return [Vector] of equatorial moon position
     */
    fun positionEquatorial(date: JulianDate): Vector {
        val t = date.julianCentury
        val l0 = frac(0.606433 + 1336.855225 * t)
        val l: Double = PI2 * frac(0.374897 + 1325.552410 * t)
        val ls: Double = PI2 * frac(0.993133 + 99.997361 * t)
        val d: Double = PI2 * frac(0.827361 + 1236.853086 * t)
        val f: Double = PI2 * frac(0.259086 + 1342.227825 * t)
        val d2 = 2.0 * d
        val l2 = 2.0 * l
        val f2 = 2.0 * f
        val dL = (
            22640.0 * sin(l) -
                4586.0 * sin(l - d2) +
                2370.0 * sin(d2) +
                769.0 * sin(l2) -
                668.0 * sin(ls) -
                412.0 * sin(f2) -
                212.0 * sin(l2 - d2) -
                206.0 * sin(l + ls - d2) +
                192.0 * sin(l + d2) -
                165.0 * sin(ls - d2) -
                125.0 * sin(d) -
                110.0 * sin(l + ls) +
                148.0 * sin(l - ls) -
                55.0 * sin(f2 - d2)
            )
        val s: Double = f + (dL + 412.0 * sin(f2) + 541.0 * sin(ls)) / ARCS
        val h = f - d2
        val n = (
            -526.0 * sin(h) +
                44.0 * sin(l + h) -
                31.0 * sin(-l + h) -
                23.0 * sin(ls + h) +
                11.0 * sin(-ls + h) -
                25.0 * sin(-l2 + f) +
                21.0 * sin(-l + f)
            )
        val lMoon: Double = PI2 * frac(l0 + dL / 1296.0e3)
        val bMoon: Double = (18520.0 * sin(s) + n) / ARCS
        val dt = 385000.5584 -
            20905.3550 * cos(l) -
            3699.1109 * cos(d2 - l) -
            2955.9676 * cos(d2) -
            569.9251 * cos(l2)
        return ofPolar(lMoon, bMoon, dt)
    }

    /**
     * Calculates the geocentric position of the moon.
     *
     * @param date
     * [JulianDate] to be used
     * @return [Vector] of geocentric moon position
     */
    fun position(date: JulianDate): Vector {
        val rotateMatrix = equatorialToEcliptical(date).transpose()
        return rotateMatrix.multiply(positionEquatorial(date))
    }

    /**
     * Calculates the horizontal position of the moon.
     *
     * @param date
     * [JulianDate] to be used
     * @param lat
     * Latitude, in radians
     * @param lng
     * Longitute, in radians
     * @return [Vector] of horizontal moon position
     */
    fun positionHorizontal(date: JulianDate, lat: Latitude, lng: Longitude): Vector {
        val mc = position(date)
        val h = date.greenwichMeanSiderealTime + lng.radians - mc.phi
        return equatorialToHorizontal(h, mc.theta, mc.r, lat)
    }

    /**
     * Returns the angular radius of the moon.
     *
     * @param distance
     * Distance of the moon, in kilometers.
     * @return Angular radius of the moon, in radians.
     * @see [Wikipedia: Angular
     * Diameter](https://en.wikipedia.org/wiki/Angular_diameter)
     */
    fun angularRadius(distance: Double): Double {
        return asin(MOON_MEAN_RADIUS / distance)
    }
}
