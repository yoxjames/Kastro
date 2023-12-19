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
import dev.jamesyox.kastro.common.HorizonMovementState
import dev.jamesyox.kastro.common.HorizonState
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class SolarStateTest {
    @Test
    fun testCologne() {
        val sp1 = LocalDateTime(2017, 7, 12, 16, 10, 0)
            .toInstant(COLOGNE_TZ)
            .calculateSolarState(COLOGNE)
        assertSimilar(actual = sp1.azimuth, expected = 239.8)
        assertSimilar(actual = sp1.altitude, expected = 48.6)
        assertSimilar(actual = sp1.trueAltitude, expected = 48.6)
        assertEquals(actual = sp1.horizonState, expected = HorizonState.Up)
        assertEquals(actual = sp1.horizonMovementState, expected = HorizonMovementState.Setting)
        val sp2 = LocalDateTime(2017, 7, 12, 13, 37, 0)
            .toInstant(COLOGNE_TZ)
            .calculateSolarState(COLOGNE)
        assertSimilar(actual = sp2.azimuth, expected = 179.6)
        assertSimilar(actual = sp2.altitude, expected = 61.0)
        assertSimilar(actual = sp2.trueAltitude, expected = 61.0)
        assertEquals(actual = sp1.horizonState, expected = HorizonState.Up)
        assertEquals(actual = sp1.horizonMovementState, expected = HorizonMovementState.Setting)
    }

    @Test
    fun testAlert() {
        val sp1 = LocalDateTime(2017, 7, 12, 6, 17, 0)
            .toInstant(ALERT_TZ)
            .calculateSolarState(ALERT)
        assertSimilar(actual = sp1.azimuth, expected = 87.5)
        assertSimilar(actual = sp1.altitude, expected = 21.8)
        assertSimilar(actual = sp1.trueAltitude, expected = 21.8)
        assertEquals(actual = sp1.horizonState, expected = HorizonState.Up)
        assertEquals(actual = sp1.horizonMovementState, expected = HorizonMovementState.Rising)
        val sp2 = LocalDateTime(2017, 7, 12, 12, 14, 0)
            .toInstant(ALERT_TZ)
            .calculateSolarState(ALERT)
        assertSimilar(actual = sp2.azimuth, expected = 179.7)
        assertSimilar(actual = sp2.altitude, expected = 29.4)
        assertSimilar(actual = sp2.trueAltitude, expected = 29.4)
        assertEquals(actual = sp1.horizonState, expected = HorizonState.Up)
        assertEquals(actual = sp1.horizonMovementState, expected = HorizonMovementState.Rising)
    }

    @Test
    fun testWellington() {
        val sp1 = LocalDateTime(2017, 7, 12, 3, 7, 0)
            .toInstant(WELLINGTON_TZ)
            .calculateSolarState(WELLINGTON)
        assertSimilar(actual = sp1.azimuth, expected = 107.3)
        assertSimilar(actual = sp1.altitude, expected = -51.3)
        assertSimilar(actual = sp1.trueAltitude, expected = -51.3)
        val sp2 = LocalDateTime(2017, 7, 12, 12, 26, 0)
            .toInstant(WELLINGTON_TZ)
            .calculateSolarState(WELLINGTON)
        assertSimilar(actual = sp2.azimuth, expected = 0.1)
        assertSimilar(actual = sp2.altitude, expected = 26.8)
        assertSimilar(actual = sp2.trueAltitude, expected = 26.8)
        val sp3 = LocalDateTime(2017, 7, 12, 7, 50, 0)
            .toInstant(WELLINGTON_TZ)
            .calculateSolarState(WELLINGTON)
        assertSimilar(actual = sp3.azimuth, expected = 60.0)
        assertSimilar(actual = sp3.altitude, expected = 0.6)
        assertSimilar(actual = sp3.trueAltitude, expected = 0.1)
    }

    @Test
    fun testPuertoWilliams() {
        val sp1 = LocalDateTime(2017, 2, 7, 18, 13, 0)
            .toInstant(PUERTO_WILLIAMS_TZ)
            .calculateSolarState(PUERTO_WILLIAMS)
        assertSimilar(actual = sp1.azimuth, expected = 280.1)
        assertSimilar(actual = sp1.altitude, expected = 25.4)
        assertSimilar(actual = sp1.trueAltitude, expected = 25.4)
        val sp2 = LocalDateTime(2017, 2, 7, 13, 44, 0)
            .toInstant(PUERTO_WILLIAMS_TZ)
            .calculateSolarState(PUERTO_WILLIAMS)
        assertSimilar(actual = sp2.azimuth, expected = 0.2)
        assertSimilar(actual = sp2.altitude, expected = 50.2)
        assertSimilar(actual = sp2.trueAltitude, expected = 50.2)
    }

    @Test
    fun testSingapore() {
        val sp1 = LocalDateTime(2017, 7, 12, 10, 19, 0)
            .toInstant(SINGAPORE_TZ)
            .calculateSolarState(SINGAPORE)
        assertSimilar(actual = sp1.azimuth, expected = 60.4)
        assertSimilar(actual = sp1.altitude, expected = 43.5)
        assertSimilar(actual = sp1.trueAltitude, expected = 43.5)
        val sp2 = LocalDateTime(2017, 7, 12, 13, 10, 0)
            .toInstant(SINGAPORE_TZ)
            .calculateSolarState(SINGAPORE)
        assertSimilar(actual = sp2.azimuth, expected = 0.2)
        assertSimilar(actual = sp2.altitude, expected = 69.4)
        assertSimilar(actual = sp2.trueAltitude, expected = 69.4)
    }

    @Test
    fun testDistance() {
        val sp1 = LocalDateTime(2017, 1, 4, 12, 37, 0)
            .toInstant(COLOGNE_TZ)
            .calculateSolarState(COLOGNE)
        assertSimilar(actual = sp1.distance, expected = 147097390.6)
        val sp2 = LocalDateTime(2017, 4, 20, 13, 31, 0)
            .toInstant(COLOGNE_TZ)
            .calculateSolarState(COLOGNE)
        assertSimilar(actual = sp2.distance, expected = 150181373.3)
        val sp3 = LocalDateTime(2017, 7, 12, 13, 37, 0)
            .toInstant(COLOGNE_TZ)
            .calculateSolarState(COLOGNE)
        assertSimilar(actual = sp3.distance, expected = 152088309.0)
        val sp4 = LocalDateTime(2017, 10, 11, 13, 18, 0)
            .toInstant(COLOGNE_TZ)
            .calculateSolarState(COLOGNE)
        assertSimilar(actual = sp4.distance, expected = 149380680.0)
    }
}
