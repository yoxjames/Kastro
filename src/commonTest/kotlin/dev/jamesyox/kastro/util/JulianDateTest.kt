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

import dev.jamesyox.kastro.assertSimilar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ERROR = 0.001

/**
 * Unit tests for [JulianDate].
 */
class JulianDateTest {
    @Test
    fun testAtHour() {
        val jd = LocalDateTime(2017, 8, 19, 0, 0, 0).toInstant(UTC).julianDate
        assertSimilar(
            actual = jd.instant,
            expected = LocalDateTime(2017,8, 19, 0, 0,0).toInstant(UTC))
        val jd2 = jd.atHour(8.5)
        assertSimilar(
            actual = jd2.instant,
            expected = LocalDateTime(2017, 8, 19, 8, 30, 0).toInstant(UTC))
        val jd3 = LocalDateTime(2017, 8, 19, 0, 0, 0)
            .toInstant(TimeZone.of("Europe/Berlin"))
            .julianDate
        assertSimilar(
            actual = jd3.instant,
            expected = LocalDateTime(2017, 8,19, 0,0,0).toInstant(TimeZone.of("Europe/Berlin")))
        val jd4 = jd3.atHour(8.5)
        assertSimilar(
            actual = jd4.instant,
            expected = LocalDateTime(2017, 8, 19, 8, 30, 0).toInstant(TimeZone.of("Europe/Berlin"))
        )
    }

    @Test
    fun testModifiedJulianDate() {
        // MJD epoch is midnight of November 17th, 1858.
        val jd1 = LocalDateTime(1858, 11, 17, 0, 0, 0).toInstant(UTC).julianDate
        assertEquals(jd1.mjd, 0.0, ERROR)
        val jd2 = LocalDateTime(2017, 8, 19, 15, 6, 16).toInstant(UTC).julianDate
        assertEquals(jd2.mjd, 57984.629, ERROR)
        val jd3 = LocalDateTime(2017, 8, 19, 15, 6, 16).toInstant(UtcOffset(2)).julianDate
        assertEquals(jd3.mjd, 57984.546, ERROR)
    }

    @Test
    fun testJulianCentury() {
        val jd1 = LocalDateTime(2000, 1, 1, 0, 0, 0).toInstant(UTC).julianDate
        assertEquals(jd1.julianCentury, 0.0, ERROR)
        val jd2 = LocalDateTime(2017, 1, 1, 0, 0, 0).toInstant(UTC).julianDate
        assertEquals(jd2.julianCentury, 0.17, ERROR)
        val jd3 = LocalDateTime(2050, 7, 1, 0, 0, 0).toInstant(UTC).julianDate
        assertEquals(jd3.julianCentury, 0.505, ERROR)
    }

    @Test
    fun testGreenwichMeanSiderealTime() {
        val jd1 = LocalDateTime(2017, 9, 3, 19, 5, 15).toInstant(UTC).julianDate
        assertEquals(jd1.greenwichMeanSiderealTime, 4.702, ERROR)
    }

    @Test
    fun testTrueAnomaly() {
        val jd1 = LocalDateTime(2017, 1, 4, 0, 0, 0).toInstant(UTC).julianDate
        assertEquals(expected = 0.0, actual = jd1.trueAnomaly, absoluteTolerance = 0.1)
        val jd2 = LocalDateTime(2017, 7, 4, 0, 0, 0).toInstant(UTC).julianDate
        assertEquals(expected = PI, actual = jd2.trueAnomaly, absoluteTolerance = 0.1)
    }

    @Test
    fun fiddleWithJulian() {
        println(Clock.System.now().julianDate.julianCentury)
    }
}