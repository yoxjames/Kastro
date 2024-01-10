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

class LunarStateTest {
    @Test
    fun testPhase() {
        val lunarState = LunarState(
            position = LunarPosition(
                distance = 397499.5405630009,
                azimuth = 38.079894702056265,
                altitude = -54.8476950985956,
                parallacticAngleRad = -0.502446475079088
            ),
            illumination = LunarIllumination(
                fraction = 0.40620985480701605,
                phase = 100.81160627293151,
                illuminationAngle = 111.39120207982295
            ),
        )

        assertEquals(expected = LunarPhase.Intermediate.WaningCrescent, actual = lunarState.phase)
    }
}