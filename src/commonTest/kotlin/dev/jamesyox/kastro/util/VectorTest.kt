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
 * Unit tests for [Vector].
 */
class VectorTest {
    companion object {
        private const val ERROR = 0.001
        private const val PI_HALF = PI / 2.0
    }

    @Test
    fun testConstructors() {
        val v1 = Vector(20.0, 10.0, 5.0)
        assertEquals(v1.x, 20.0)
        assertEquals(v1.y, 10.0)
        assertEquals(v1.z, 5.0)
        val v2 = Vector(20.0, 10.0, 5.0)
        assertEquals(v2.x, 20.0)
        assertEquals(v2.y, 10.0)
        assertEquals(v2.z, 5.0)
        val v3 = Vector.ofPolar(0.5, 0.25)
        assertEquals(v3.phi, 0.5)
        assertEquals(v3.theta, 0.25)
        assertEquals(v3.r, 1.0)
        val v4 = Vector.ofPolar(0.5, 0.25, 50.0)
        assertEquals(v4.phi, 0.5, ERROR)
        assertEquals(v4.theta, 0.25, ERROR)
        assertEquals(v4.r, 50.0, ERROR)
    }

    @Test
    fun testAdd() {
        val v1 = Vector(20.0, 10.0, 5.0)
        val v2 = Vector(10.0, 25.0, 15.0)
        val r1 = v1 + v2
        assertEquals(r1.x, 30.0)
        assertEquals(r1.y, 35.0)
        assertEquals(r1.z, 20.0)
        val r2 = v2 + v1
        assertEquals(r2.x, 30.0)
        assertEquals(r2.y, 35.0)
        assertEquals(r2.z, 20.0)
    }

    @Test
    fun testSubtract() {
        val v1 = Vector(20.0, 10.0, 5.0)
        val v2 = Vector(10.0, 25.0, 15.0)
        val r1 = v1 - v2
        assertEquals(r1.x, 10.0)
        assertEquals(r1.y, -15.0)
        assertEquals(r1.z, -10.0)
        val r2 = v2 - v1
        assertEquals(r2.x, -10.0)
        assertEquals(r2.y, 15.0)
        assertEquals(r2.z, 10.0)
    }

    @Test
    fun testMultiply() {
        val v1 = Vector(20.0, 10.0, 5.0)
        val r1 = v1 * 5.0
        assertEquals(r1.x, 100.0)
        assertEquals(r1.y, 50.0)
        assertEquals(r1.z, 25.0)
    }

    @Test
    fun testNegate() {
        val v1 = Vector(20.0, 10.0, 5.0)
        val r1 = -v1
        assertEquals(r1.x, -20.0)
        assertEquals(r1.y, -10.0)
        assertEquals(r1.z, -5.0)
    }

    @Test
    fun testCross() {
        val v1 = Vector(3.0, -3.0, 1.0)
        val v2 = Vector(4.0, 9.0, 2.0)
        val r1 = v1.cross(v2)
        assertEquals(r1.x, -15.0)
        assertEquals(r1.y, -2.0)
        assertEquals(r1.z, 39.0)
    }

    @Test
    fun testDot() {
        val v1 = Vector(1.0, 2.0, 3.0)
        val v2 = Vector(4.0, -5.0, 6.0)
        val r1 = v1.dot(v2)
        assertEquals(r1, 12.0, ERROR)
    }

    @Test
    fun testNorm() {
        val v1 = Vector(5.0, -6.0, 7.0)
        val r1 = v1.norm()
        assertEquals(r1, 10.488, ERROR)
    }

    @Test
    fun testEquals() {
        val v1 = Vector(3.0, -3.0, 1.0)
        val v2 = Vector(4.0, 9.0, 2.0)
        val v3 = Vector(3.0, -3.0, 1.0)
        assertNotEquals(v1, v2)
        assertEquals(v1, v3)
        assertNotEquals(v2, v3)
        assertEquals(v3, v1)
        assertNotNull(v1)
        assertNotEquals(v1, Any())
    }

    @Test
    fun testHashCode() {
        val h1 = Vector(3.0, -3.0, 1.0).hashCode()
        val h2 = Vector(4.0, 9.0, 2.0).hashCode()
        val h3 = Vector(3.0, -3.0, 1.0).hashCode()
        assertNotEquals(h1, 0)
        assertNotEquals(h2, 0)
        assertNotEquals(h3, 0)
        assertNotEquals(h1, h2)
        assertEquals(h1, h3)
    }

    @Test
    fun testToCartesian() {
        val v1 = Vector.ofPolar(0.0, 0.0)
        assertEquals(v1.x, 1.0, ERROR)
        assertEquals(v1.y, 0.0, ERROR)
        assertEquals(v1.z, 0.0, ERROR)
        val v2 = Vector.ofPolar(PI_HALF, 0.0)
        assertEquals(v2.x, 0.0, ERROR)
        assertEquals(v2.y, 1.0, ERROR)
        assertEquals(v2.z, 0.0, ERROR)
        val v3 = Vector.ofPolar(0.0, PI_HALF)
        assertEquals(v3.x, 0.0, ERROR)
        assertEquals(v3.y, 0.0, ERROR)
        assertEquals(v3.z, 1.0, ERROR)
        val v4 = Vector.ofPolar(PI_HALF, PI_HALF)
        assertEquals(v4.x, 0.0, ERROR)
        assertEquals(v4.y, 0.0, ERROR)
        assertEquals(v4.z, 1.0, ERROR)
        val v5 = Vector.ofPolar(PI_HALF, -PI_HALF)
        assertEquals(v5.x, 0.0, ERROR)
        assertEquals(v5.y, 0.0, ERROR)
        assertEquals(v5.z, -1.0, ERROR)
        val v6 = Vector.ofPolar(0.0, 0.0, 5.0)
        assertEquals(v6.x, 5.0, ERROR)
        assertEquals(v6.y, 0.0, ERROR)
        assertEquals(v6.z, 0.0, ERROR)
        val v7 = Vector.ofPolar(PI_HALF, 0.0, 5.0)
        assertEquals(v7.x, 0.0, ERROR)
        assertEquals(v7.y, 5.0, ERROR)
        assertEquals(v7.z, 0.0, ERROR)
        val v8 = Vector.ofPolar(0.0, PI_HALF, 5.0)
        assertEquals(v8.x, 0.0, ERROR)
        assertEquals(v8.y, 0.0, ERROR)
        assertEquals(v8.z, 5.0, ERROR)
        val v9 = Vector.ofPolar(PI_HALF, PI_HALF, 5.0)
        assertEquals(v9.x, 0.0, ERROR)
        assertEquals(v9.y, 0.0, ERROR)
        assertEquals(v9.z, 5.0, ERROR)
        val v10 = Vector.ofPolar(PI_HALF, -PI_HALF, 5.0)
        assertEquals(v10.x, 0.0, ERROR)
        assertEquals(v10.y, 0.0, ERROR)
        assertEquals(v10.z, -5.0, ERROR)
    }

    @Test
    fun testToPolar() {
        val v1 = Vector(20.0, 0.0, 0.0)
        assertEquals(v1.phi, 0.0)
        assertEquals(v1.theta, 0.0)
        assertEquals(v1.r, 20.0)
        val v2 = Vector(0.0, 20.0, 0.0)
        assertEquals(v2.phi, PI_HALF)
        assertEquals(v2.theta, 0.0)
        assertEquals(v2.r, 20.0)
        val v3 = Vector(0.0, 0.0, 20.0)
        assertEquals(v3.phi, 0.0)
        assertEquals(v3.theta, PI_HALF)
        assertEquals(v3.r, 20.0)
        val v4 = Vector(-20.0, 0.0, 0.0)
        assertEquals(v4.phi, PI)
        assertEquals(v4.theta, 0.0)
        assertEquals(v4.r, 20.0)
        val v5 = Vector(0.0, -20.0, 0.0)
        assertEquals(v5.phi, PI + PI_HALF)
        assertEquals(v5.theta, 0.0)
        assertEquals(v5.r, 20.0)
        val v6 = Vector(0.0, 0.0, -20.0)
        assertEquals(v6.phi, 0.0)
        assertEquals(v6.theta, -PI_HALF)
        assertEquals(v6.r, 20.0)
        val v7 = Vector(0.0, 0.0, 0.0)
        assertEquals(v7.phi, 0.0)
        assertEquals(v7.theta, 0.0)
        assertEquals(v7.r, 0.0)
    }
}
