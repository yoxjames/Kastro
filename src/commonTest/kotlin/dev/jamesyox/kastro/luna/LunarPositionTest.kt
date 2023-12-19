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

import dev.jamesyox.kastro.Locations.ALERT
import dev.jamesyox.kastro.Locations.ALERT_TZ
import dev.jamesyox.kastro.Locations.COLOGNE
import dev.jamesyox.kastro.Locations.COLOGNE_TZ
import dev.jamesyox.kastro.Locations.PUERTO_WILLIAMS
import dev.jamesyox.kastro.Locations.PUERTO_WILLIAMS_TZ
import dev.jamesyox.kastro.Locations.SINGAPORE
import dev.jamesyox.kastro.Locations.SINGAPORE_TZ
import dev.jamesyox.kastro.Locations.WELLINGTON
import dev.jamesyox.kastro.Locations.WELLINGTON_TZ
import dev.jamesyox.kastro.assertSimilar
import kotlin.test.Test
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant

private const val DISTANCE_ERROR = 800.0

class LunarPositionTest {
    @Test
    fun testCologne() {
        val mp1 = LocalDateTime(2017, 7, 12, 13, 28, 0)
            .toInstant(COLOGNE_TZ)
            .calculateLunarPosition(COLOGNE)
        assertSimilar(actual = mp1.azimuth, expected = 304.8)
        assertSimilar(actual = mp1.altitude, expected = -39.6)
        assertSimilar(actual = mp1.parallacticAngle, expected = 32.0)
        val mp2 = LocalDateTime(2017, 7, 12, 3, 51, 0)
            .toInstant(COLOGNE_TZ)
            .calculateLunarPosition(COLOGNE)
        assertSimilar(actual = mp2.azimuth, expected = 179.9)
        assertSimilar(actual = mp2.altitude, expected = 25.3,)
        assertSimilar(actual = mp2.distance, expected = 394709.0, tolerance = DISTANCE_ERROR)
        assertSimilar(actual = mp2.parallacticAngle, expected = 0.0)
    }

    @Test
    fun testAlert() {
        val mp1 = LocalDateTime(2017, 7, 12, 8, 4, 0)
            .toInstant(ALERT_TZ)
            .calculateLunarPosition(ALERT)
        assertSimilar(actual = mp1.azimuth, expected = 257.5)
        assertSimilar(actual = mp1.altitude, expected = -10.9)
        assertSimilar(actual = mp1.parallacticAngle, expected = 7.5)
        val mp2 = LocalDateTime(2017, 7, 12, 2, 37, 0)
            .toInstant(ALERT_TZ)
            .calculateLunarPosition(ALERT)
        assertSimilar(actual = mp2.azimuth, expected = 179.8)
        assertSimilar(actual = mp2.altitude, expected = -5.7)
        assertSimilar(actual = mp2.distance, expected = 393609.0, tolerance = DISTANCE_ERROR)
        assertSimilar(actual = mp2.parallacticAngle, expected = 0.0)
    }

    @Test
    fun testWellington() {
        val mp1 = LocalDateTime(2017, 7, 12, 4, 7, 0)
            .toInstant(WELLINGTON_TZ)
            .calculateLunarPosition(WELLINGTON)
        assertSimilar(actual = mp1.azimuth, expected = 311.3)
        assertSimilar(actual = mp1.altitude, expected = 55.1)
        assertSimilar(actual = mp1.parallacticAngle, expected = 144.2)
        val mp2 = LocalDateTime(2017, 7, 12, 2, 17, 0)
            .toInstant(WELLINGTON_TZ)
            .calculateLunarPosition(WELLINGTON)
        assertSimilar(actual = mp2.azimuth, expected = 0.5)
        assertSimilar(actual = mp2.altitude, expected = 63.9)
        assertSimilar(actual = mp2.distance, expected = 396272.0, tolerance = DISTANCE_ERROR)
        assertSimilar(actual = mp2.parallacticAngle, expected = -179.6)
    }

    @Test
    fun testPuertoWilliams() {
        val mp1 = LocalDateTime(2017, 2, 7, 9, 44, 0)
            .toInstant(PUERTO_WILLIAMS_TZ)
            .calculateLunarPosition(PUERTO_WILLIAMS)
        assertSimilar(actual = mp1.azimuth, expected = 199.4)
        assertSimilar(actual = mp1.altitude, expected = -52.7)
        assertSimilar(actual = mp1.parallacticAngle, expected = 168.3)
        val mp2 = LocalDateTime(2017, 2, 7, 23, 4, 0)
            .toInstant(PUERTO_WILLIAMS_TZ)
            .calculateLunarPosition(PUERTO_WILLIAMS)
        assertSimilar(actual = mp2.azimuth, expected = 0.1)
        assertSimilar(actual = mp2.altitude, expected = 16.3)
        assertSimilar(actual = mp2.distance, expected = 369731.0, tolerance = DISTANCE_ERROR)
        assertSimilar(actual = mp2.parallacticAngle, expected = -179.9)
    }

    @Test
    fun testSingapore() {
        val mp1 = LocalDateTime(2017, 7, 12, 5, 12, 0)
            .toInstant(SINGAPORE_TZ)
            .calculateLunarPosition(SINGAPORE)
        assertSimilar(actual = mp1.azimuth, expected = 240.6)
        assertSimilar(actual = mp1.altitude, expected = 57.1)
        assertSimilar(actual = mp1.parallacticAngle, expected = 64.0)
        val mp2 = LocalDateTime(2017, 7, 12, 3, 11, 0)
            .toInstant(SINGAPORE_TZ)
            .calculateLunarPosition(SINGAPORE)
        assertSimilar(actual = mp2.azimuth, expected = 180.0)
        assertSimilar(actual = mp2.altitude, expected = 74.1)
        assertSimilar(actual = mp2.distance, expected = 395621.0, tolerance = DISTANCE_ERROR)
        assertSimilar(actual = mp2.parallacticAngle, expected = 0.0)
    }
}