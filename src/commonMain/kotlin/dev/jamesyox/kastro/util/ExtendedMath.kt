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
@file:Suppress("TooManyFunctions")

package dev.jamesyox.kastro.util

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.roundToLong
import kotlin.math.sign
import kotlin.math.tan

/**
 * Contains constants and mathematical operations that are not available in the Kotlin Standard Library.
 */
internal object ExtendedMath {
    /**
     * PI * 2
     */
    internal const val PI2 = PI * 2.0

    /**
     * Arc-Seconds per Radian.
     */
    val ARCS = 3600.0.degrees

    /**
     * Mean radius of the earth, in kilometers.
     */
    private const val EARTH_MEAN_RADIUS = 6371.0

    /**
     * Returns the decimal part of a value.
     *
     * @param a Value to extract fraction from
     * @return Fraction of that value. It has the same sign as the input value.
     */
    fun frac(a: Double): Double = a % 1.0

    /**
     * Performs a safe check if the given double is actually zero (0.0).
     *
     *
     * Note that "almost zero" returns `false`, so this method should not be used
     * for comparing calculation results to zero.
     *
     * @param d
     * double to check for zero.
     * @return `true` if the value was zero, or negative zero.
     */
    fun isZero(d: Double): Boolean {
        // This should keep squid:S1244 silent...
        return !d.isNaN() && sign(d).roundToLong() == 0L
    }

    /**
     * Converts equatorial coordinates to horizontal coordinates.
     *
     * @param tau
     * Hour angle (radians)
     * @param dec
     * Declination (radians)
     * @param dist
     * Distance of the object
     * @param lat
     * Latitude of the observer (radians)
     * @return [Vector] containing the horizontal coordinates
     */
    fun equatorialToHorizontal(tau: Double, dec: Double, dist: Double, lat: Latitude): Vector {
        return Matrix.rotateY(PI / 2.0 - lat.radians).multiply(Vector.ofPolar(tau, dec, dist))
    }

    /**
     * Creates a rotational [Matrix] for converting equatorial to ecliptical
     * coordinates.
     *
     * @param t [JulianDate] to use
     * @return [Matrix] for converting equatorial to ecliptical coordinates
     */
    fun equatorialToEcliptical(t: JulianDate): Matrix {
        val jc = t.julianCentury
        val eps = (23.43929111 - (46.8150 + (0.00059 - 0.001813 * jc) * jc) * jc / 3600.0).radians
        return Matrix.rotateX(eps)
    }

    /**
     * Returns the parallax for objects at the horizon.
     *
     * @param height
     * Observer's height, in meters above sea level. Must not be negative.
     * @param distance
     * Distance of the sun, in kilometers.
     * @return parallax, in radians
     */
    fun parallax(height: Double, distance: Double): Double {
        return (
            asin(EARTH_MEAN_RADIUS / distance) -
                acos(EARTH_MEAN_RADIUS / (EARTH_MEAN_RADIUS + (height / 1000.0)))
            )
    }

    /**
     * Calculates the atmospheric refraction of an object at the given apparent altitude.
     *
     * The result is only valid for positive altitude angles. If negative, 0.0 is
     * returned.
     *
     * Assumes an atmospheric pressure of 1010 hPa and a temperature of 10 °C.
     *
     * See [Wikipedia: Atmospheric Refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction)
     *
     * @param ha Apparent altitude, in radians.
     * @return Refraction at this altitude
     */
    fun apparentRefraction(ha: Double): Double {
        return if (ha < 0.0) 0.0 else (PI / (tan((ha + 7.31 / (ha + 4.4)).radians) * 10800.0))
    }

    /**
     * Calculates the atmospheric refraction of an object at the given altitude.
     *
     *
     * The result is only valid for positive altitude angles. If negative, 0.0 is
     * returned.
     *
     *
     * Assumes an atmospheric pressure of 1010 hPa and a temperature of 10 °C.
     *
     * @param h
     * True altitude, in radians.
     * @return Refraction at this altitude
     * @see [Wikipedia:
     * Atmospheric Refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction)
     */
    fun refraction(h: Double): Double {
        return if (h < 0.0) 0.0 else (0.000296706 / tan(h + 0.00312537 / (h + 0.0890118)))
    }

    /**
     * Converts dms to double.
     *
     * @param d
     * Degrees. Sign is used for result.
     * @param m
     * Minutes. Sign is ignored.
     * @param s
     * Seconds and fractions. Sign is ignored.
     * @return angle, in degrees
     */
    fun dms(d: Int, m: Int, s: Double): Double {
        val sig = if (d < 0) -1.0 else 1.0
        return sig * ((abs(s) / 60.0 + abs(m)) / 60.0 + abs(d))
    }

    /**
     * Locates the true maximum within the given time frame.
     *
     * @param time
     * Base time
     * @param frame
     * Time frame, which is added to and subtracted from the base time for the
     * interval
     * @param depth
     * Maximum recursion depth. For each recursion, the function is invoked once.
     * @param f
     * Function to be used for calculation
     * @return time of the true maximum
     */
    fun readjustMax(time: Double, frame: Double, depth: Int, f: (Double) -> Double): Double {
        val left = time - frame
        val right = time + frame
        val leftY = f(left)
        val rightY = f(right)
        return readjustInterval(left, right, leftY, rightY, depth, f) { d1, d2 -> d1.compareTo(d2) }
    }

    /**
     * Locates the true minimum within the given time frame.
     *
     * @param time
     * Base time
     * @param frame
     * Time frame, which is added to and subtracted from the base time for the
     * interval
     * @param depth
     * Maximum recursion depth. For each recursion, the function is invoked once.
     * @param f
     * Function to be used for calculation
     * @return time of the true minimum
     */
    fun readjustMin(time: Double, frame: Double, depth: Int, f: (Double) -> Double): Double {
        val left = time - frame
        val right = time + frame
        val leftY = f(left)
        val rightY = f(right)
        return readjustInterval(left, right, leftY, rightY, depth, f) { yl, yr -> yr.compareTo(yl) }
    }

    /**
     * Recursively find the true maximum/minimum within the given time frame.
     *
     * @param left
     * Left interval border
     * @param right
     * Right interval border
     * @param yl
     * Function result at the left interval
     * @param yr
     * Function result at the right interval
     * @param depth
     * Maximum recursion depth. For each recursion, the function is invoked once.
     * @param f
     * Function to invoke
     * @param cmp
     * Comparator to decide whether the left or right side of the interval half is
     * to be used
     * @return Position of the approximated minimum/maximum
     */
    @Suppress("LongParameterList")
    private tailrec fun readjustInterval(
        left: Double,
        right: Double,
        yl: Double,
        yr: Double,
        depth: Int,
        f: (Double) -> Double,
        cmp: Comparator<Double>
    ): Double {
        if (depth <= 0) {
            return if (cmp.compare(yl, yr) < 0) right else left
        }
        val middle = (left + right) / 2.0
        val ym = f(middle)
        return if (cmp.compare(yl, yr) < 0) {
            readjustInterval(middle, right, ym, yr, depth - 1, f, cmp)
        } else {
            readjustInterval(left, middle, yl, ym, depth - 1, f, cmp)
        }
    }
}
