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
 * Unit tests for [QuadraticInterpolation].
 */
class QuadraticInterpolationTest {
    @Test
    fun testTwoRootsAndMinimum() {
        val qi = QuadraticInterpolation.of(1.0, -1.0, 1.0)
        assertEquals(qi.numberOfRoots, 2)
        assertEquals(qi.root1, -0.707, ERROR)
        assertEquals(qi.root2, 0.707, ERROR)
        assertEquals(qi.xe, 0.0, ERROR)
        assertEquals(qi.ye, -1.0, ERROR)
        assertFalse(qi.isMaximum)
    }

    @Test
    fun testTwoRootsAndMaximum() {
        val qi = QuadraticInterpolation.of(-1.0, 1.0, -1.0)
        assertEquals(qi.numberOfRoots, 2)
        assertEquals(qi.root1, -0.707, ERROR)
        assertEquals(qi.root2, 0.707, ERROR)
        assertEquals(qi.xe, 0.0, ERROR)
        assertEquals(qi.ye, 1.0, ERROR)
        assertTrue(qi.isMaximum)
    }

    @Test
    fun testOneRoot() {
        val qi = QuadraticInterpolation.of(2.0, 0.0, -1.0)
        assertEquals(qi.numberOfRoots, 1)
        assertEquals(qi.root1, 0.0, ERROR)
        assertEquals(qi.xe, 1.5, ERROR)
        assertEquals(qi.ye, -1.125, ERROR)
        assertFalse(qi.isMaximum)
    }

    @Test
    fun testNoRoot() {
        val qi = QuadraticInterpolation.of(3.0, 2.0, 1.0)
        assertEquals(qi.numberOfRoots, 0)
    }
}
