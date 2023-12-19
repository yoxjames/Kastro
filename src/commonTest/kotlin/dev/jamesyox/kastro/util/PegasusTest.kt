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
import kotlin.test.assertFailsWith
import kotlin.test.fail

private const val ERROR = 0.001
/**
 * Unit tests for [Pegasus].
 */
class PegasusTest {
    @Test
    fun testParabola() {
        // f(x) = x^2 + 2x - 3
        // Roots at x = -3 and x = 1
        val parabola = { x: Double -> x * x + 2 * x - 3 }
        val r1 = Pegasus.calculate(0.0, 3.0, 0.1, parabola)
        assertEquals(r1, 1.0, ERROR)
        val r2 = Pegasus.calculate(-5.0, 0.0, 0.1, parabola)
        assertEquals(r2, -3.0, ERROR)
        try {
            Pegasus.calculate(-2.0, 0.5, 0.1, parabola)
            fail("Found a non-existing root")
        } catch (ex: ArithmeticException) {
            // expected
        }
    }

    @Test
    fun testParabola2() {
        assertFailsWith<ArithmeticException> {
            // f(x) = x^2 + 3
            // No roots
            Pegasus.calculate(-5.0, 5.0, 0.1) { x: Double -> x * x + 3 }
        }
    }
}