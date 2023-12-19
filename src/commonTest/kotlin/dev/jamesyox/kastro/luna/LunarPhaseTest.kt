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

import kotlin.test.Test
import kotlin.test.assertEquals

class LunarPhaseTest {
    @Test
    fun testClosestMoonPhase() {
        // exact angles
        assertEquals(actual = LunarPhase.closestMoonPhase(0.0), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(45.0), expected = LunarPhase.Intermediate.WaxingCrescent)
        assertEquals(actual = LunarPhase.closestMoonPhase(90.0), expected = LunarEvent.PhaseEvent.FirstQuarter)
        assertEquals(actual = LunarPhase.closestMoonPhase(135.0), expected = LunarPhase.Intermediate.WaxingGibbous)
        assertEquals(actual = LunarPhase.closestMoonPhase(180.0), expected = LunarEvent.PhaseEvent.FullMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(225.0), expected = LunarPhase.Intermediate.WaningGibbous)
        assertEquals(actual = LunarPhase.closestMoonPhase(270.0), expected = LunarEvent.PhaseEvent.LastQuarter)
        assertEquals(actual = LunarPhase.closestMoonPhase(315.0), expected = LunarPhase.Intermediate.WaningCrescent)

        // out of range angles (normalization test)
        assertEquals(actual = LunarPhase.closestMoonPhase(360.0), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(720.0), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(-360.0), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(-720.0), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(855.0), expected = LunarPhase.Intermediate.WaxingGibbous)
        assertEquals(actual = LunarPhase.closestMoonPhase(-585.0), expected = LunarPhase.Intermediate.WaxingGibbous)
        assertEquals(actual = LunarPhase.closestMoonPhase(-945.0), expected = LunarPhase.Intermediate.WaxingGibbous)

        // close to boundary
        assertEquals(actual = LunarPhase.closestMoonPhase(22.4), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(67.4), expected = LunarPhase.Intermediate.WaxingCrescent)
        assertEquals(actual = LunarPhase.closestMoonPhase(112.4), expected =  LunarEvent.PhaseEvent.FirstQuarter)
        assertEquals(actual = LunarPhase.closestMoonPhase(157.4), expected =  LunarPhase.Intermediate.WaxingGibbous)
        assertEquals(actual = LunarPhase.closestMoonPhase(202.4), expected =  LunarEvent.PhaseEvent.FullMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(247.4), expected =  LunarPhase.Intermediate.WaningGibbous)
        assertEquals(actual = LunarPhase.closestMoonPhase(292.4), expected =  LunarEvent.PhaseEvent.LastQuarter)
        assertEquals(actual = LunarPhase.closestMoonPhase(337.4), expected =  LunarPhase.Intermediate.WaningCrescent)
        assertEquals(actual = LunarPhase.closestMoonPhase(382.4), expected =  LunarEvent.PhaseEvent.NewMoon)

        assertEquals(actual = LunarPhase.closestMoonPhase(-0.00000001), expected = LunarEvent.PhaseEvent.NewMoon)
        assertEquals(actual = LunarPhase.closestMoonPhase(0.000000001), expected = LunarEvent.PhaseEvent.NewMoon)
    }

    @Test
    fun testLunarPhase() {
        // At boundary:
        // If we try to get the lunar phase at exactly 0.0 (New Moon) then we expect to get WaxingCresent since the
        // New Moon is an instant in time and code takes some time to execute returning WaxingCrescent makes the most
        // sense here.
        assertEquals(actual = LunarPhase.lunarPhase(0.0), expected = LunarPhase.Intermediate.WaxingCrescent)

        // Staying consistent with above the angle of First Quarter would map to Waxing Crescent
        assertEquals(actual = LunarPhase.lunarPhase(90.0), expected = LunarPhase.Intermediate.WaxingGibbous)

        // So staying consistent with the above logic, we would expect 180.0 (Full Moon) to return WaningGibbous as the
        // current "state."
        assertEquals(actual = LunarPhase.lunarPhase(180.0), expected = LunarPhase.Intermediate.WaningGibbous)

        // Staying consistent with the above, 270.0 should be a Waning Crescent
        assertEquals(actual = LunarPhase.lunarPhase(270.0), expected = LunarPhase.Intermediate.WaningCrescent)

        // Happy path
        assertEquals(actual = LunarPhase.lunarPhase(45.0), expected = LunarPhase.Intermediate.WaxingCrescent)
        assertEquals(actual = LunarPhase.lunarPhase(135.0), expected = LunarPhase.Intermediate.WaxingGibbous)
        assertEquals(actual = LunarPhase.lunarPhase(225.0), expected = LunarPhase.Intermediate.WaningGibbous)
        assertEquals(actual = LunarPhase.lunarPhase(315.0), expected = LunarPhase.Intermediate.WaningCrescent)

        // 360.0 oddball
        // We want this to behave the same as lunarPhase(0.0)
        assertEquals(actual = LunarPhase.lunarPhase(360.0), expected = LunarPhase.Intermediate.WaxingCrescent)
    }
}