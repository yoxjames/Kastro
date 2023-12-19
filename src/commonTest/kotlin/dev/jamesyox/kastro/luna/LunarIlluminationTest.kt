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

import dev.jamesyox.kastro.Locations.COLOGNE_TZ
import dev.jamesyox.kastro.assertSimilar
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.NewMoon
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.FirstQuarter
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.FullMoon
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.LastQuarter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class LunarIlluminationTest {
    @Test
    fun testNewMoon() {
        val mi: LunarIllumination = LocalDateTime(2017, 6, 24, 4, 30, 0)
            .toInstant(COLOGNE_TZ)
            .calculateLunarIllumination()
        assertSimilar(actual = mi.fraction, expected = 0.0)
        assertSimilar(actual = mi.phase, expected = 176.0)
        assertSimilar(actual = mi.angle, expected = 2.0)
        assertEquals(actual = mi.closestPhase, expected = NewMoon)
    }

    @Test
    fun testWaxingHalfMoon() {
        val mi = LocalDateTime(2017, 7, 1, 2, 51, 0)
            .toInstant(COLOGNE_TZ)
            .calculateLunarIllumination()
        assertSimilar(actual = mi.fraction, expected = 0.5)
        assertSimilar(actual = mi.phase, expected = -90.0)
        assertSimilar(actual = mi.angle, expected = -66.9)
        assertEquals(actual = mi.closestPhase, expected = FirstQuarter)
    }

    @Test
    fun testFullMoon() {
        val mi = LocalDateTime(2017, 7, 9, 6, 6, 0)
            .toInstant(COLOGNE_TZ)
            .calculateLunarIllumination()
        assertSimilar(actual = mi.fraction, expected =  1.0)
        assertSimilar(actual = mi.phase, expected = -3.2) // 0.0
        assertSimilar(actual = mi.angle, expected = -7.4)
        assertEquals(actual = mi.closestPhase, expected = FullMoon)
    }

    @Test
    fun testWaningHalfMoon() {
        val mi = LocalDateTime(2017, 7, 16, 21, 25, 0)
            .toInstant(COLOGNE_TZ)
            .calculateLunarIllumination()
        assertSimilar(actual = mi.fraction, expected = 0.5)
        assertSimilar(actual = mi.phase, expected = 90.0)
        assertSimilar(actual = mi.angle, expected = 68.7)
        assertEquals(actual = mi.closestPhase, expected = LastQuarter)
    }
}
