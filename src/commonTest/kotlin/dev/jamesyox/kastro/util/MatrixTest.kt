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

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

/**
 * Unit tests for [Matrix].
 */
class MatrixTest {
    companion object {
        private val ERROR = 0.001
        private const val PI_HALF = PI / 2.0
    }

    @Test
    fun testIdentity() {
        val mx = Matrix.identity()
        assertValues(
            mx,
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0
        )
    }

    @Test
    fun testRotateX() {
        val mx = Matrix.rotateX(PI_HALF)
        assertValues(
            mx,
            1.0, 0.0, 0.0,
            0.0, 0.0, 1.0,
            0.0, -1.0, 0.0
        )
    }

    @Test
    fun testRotateY() {
        val mx = Matrix.rotateY(PI_HALF)
        assertValues(
            mx,
            0.0, 0.0, -1.0,
            0.0, 1.0, 0.0,
            1.0, 0.0, 0.0
        )
    }

    @Test
    fun testRotateZ() {
        val mx = Matrix.rotateZ(PI_HALF)
        assertValues(
            mx,
            0.0, 1.0, 0.0,
            -1.0, 0.0, 0.0,
            0.0, 0.0, 1.0
        )
    }

    @Test
    fun testTranspose() {
        val mx = Matrix.rotateX(PI_HALF).transpose()
        assertValues(
            mx,
            1.0, 0.0, 0.0,
            0.0, 0.0, -1.0,
            0.0, 1.0, 0.0
        )
    }

    @Test
    fun testNegate() {
        val mx = Matrix.identity().negate()
        assertValues(
            mx,
            -1.0, 0.0, 0.0,
            0.0, -1.0, 0.0,
            0.0, 0.0, -1.0
        )
    }

    @Test
    fun testAdd() {
        val mx1 = Matrix.rotateX(PI_HALF)
        val mx2 = Matrix.rotateY(PI_HALF)
        assertValues(
            mx1 + mx2,
            1.0, 0.0, -1.0,
            0.0, 1.0, 1.0,
            1.0, -1.0, 0.0
        )
        assertValues(
            mx2 + mx1,
            1.0, 0.0, -1.0,
            0.0, 1.0, 1.0,
            1.0, -1.0, 0.0
        )
    }

    @Test
    fun testSubtract() {
        val mx1 = Matrix.rotateX(PI_HALF)
        val mx2 = Matrix.rotateY(PI_HALF)
        assertValues(
            mx1 - mx2,
            1.0, 0.0, 1.0,
            0.0, -1.0, 1.0,
            -1.0, -1.0, 0.0
        )
        assertValues(
            mx2 - mx1,
            -1.0, 0.0, -1.0,
            0.0, 1.0, -1.0,
            1.0, 1.0, 0.0
        )
    }

    @Test
    fun testMultiply() {
        val mx1 = Matrix.rotateX(PI_HALF)
        val mx2 = Matrix.rotateY(PI_HALF)
        assertValues(
            mx1.multiply(mx2),
            0.0, 0.0, -1.0,
            1.0, 0.0, 0.0,
            0.0, -1.0, 0.0
        )
        assertValues(
            mx2.multiply(mx1),
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0,
            1.0, 0.0, 0.0
        )
    }

    @Test
    fun testScalarMultiply() {
        val mx = Matrix.identity().multiply(5.0)
        assertValues(
            mx,
            5.0, 0.0, 0.0,
            0.0, 5.0, 0.0,
            0.0, 0.0, 5.0
        )
    }

    @Test
    fun testVectorMultiply() {
        val mx = Matrix.rotateX(PI_HALF)
        val vc = Vector(5.0, 8.0, -3.0)
        val result = mx.multiply(vc)
        assertEquals(result.x,5.0, ERROR)
        assertEquals(result.y, -3.0, ERROR)
        assertEquals(result.z, -8.0, ERROR)
    }

    @Test
    fun testEquals() {
        val mx1 = Matrix.identity()
        val mx2 = Matrix.rotateX(PI_HALF)
        val mx3 = Matrix.identity()
        assertNotEquals(mx1, mx2)
        assertEquals(mx1, mx3)
        assertNotEquals(mx2, mx3)
        assertEquals(mx3, mx1)
        assertNotNull(mx1)
        assertNotEquals(mx1, Any())
    }

    @Test
    fun testHashCode() {
        val mx1 = Matrix.identity().hashCode()
        val mx2 = Matrix.rotateX(PI_HALF).hashCode()
        val mx3 = Matrix.identity().hashCode()
        assertNotEquals(0, mx1)
        assertNotEquals(0, mx2)
        assertNotEquals(0, mx3)
        assertNotEquals(mx2, mx1)
        assertEquals(mx1, mx3)
    }

    private fun assertValues(mx: Matrix, vararg values: Double) {
        for (ix in values.indices) {
            val r = ix / 3
            val c = ix % 3
            assertEquals(mx[r, c], values[ix], ERROR)
        }
    }
}
