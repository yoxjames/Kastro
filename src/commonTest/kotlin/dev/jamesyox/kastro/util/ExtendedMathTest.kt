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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val ERROR = 0.001
/**
 * Unit tests for [ExtendedMath].
 */
class ExtendedMathTest {
    @Test
    fun testFrac() {
        assertEquals(ExtendedMath.frac(1.0), 0.0, ERROR)
        assertEquals(ExtendedMath.frac(0.5), 0.5, ERROR)
        assertEquals(ExtendedMath.frac(123.25), 0.25, ERROR)
        assertEquals(ExtendedMath.frac(0.0), 0.0, ERROR)
        assertEquals(ExtendedMath.frac(-1.0), 0.0, ERROR)
        assertEquals(ExtendedMath.frac(-0.5), -0.5, ERROR)
        assertEquals(ExtendedMath.frac(-123.25), -0.25, ERROR)
    }

    @Test
    fun testIsZero() {
        assertFalse(ExtendedMath.isZero(1.0))
        assertFalse(ExtendedMath.isZero(0.0001))
        assertTrue(ExtendedMath.isZero(0.0))
        assertTrue(ExtendedMath.isZero(-0.0))
        assertFalse(ExtendedMath.isZero(-0.0001))
        assertFalse(ExtendedMath.isZero(-1.0))
        assertFalse(ExtendedMath.isZero(Double.NaN))
        assertFalse(ExtendedMath.isZero(-Double.NaN))
        assertFalse(ExtendedMath.isZero(Double.POSITIVE_INFINITY))
        assertFalse(ExtendedMath.isZero(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun testDms() {
        // Valid parameters
        assertEquals(ExtendedMath.dms(0, 0, 0.0), 0.0)
        assertEquals(ExtendedMath.dms(13, 27, 4.32), 13.4512)
        assertEquals(ExtendedMath.dms(-88, 39, 8.28), -88.6523)

        // Sign at wrong position is ignored
        assertEquals(ExtendedMath.dms(14, -14, 2.4), 14.234)
        assertEquals(ExtendedMath.dms(66, 12, -46.8), 66.213)

        // Out of range values are carried to the next position
        assertEquals(ExtendedMath.dms(0, 0, 72.0), 0.02) // 0°  1' 12.0"
        assertEquals(ExtendedMath.dms(1, 80, 132.0), 2.37) // 2° 22' 12.0"
    }
}