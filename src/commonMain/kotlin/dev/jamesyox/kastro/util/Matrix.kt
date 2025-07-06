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
@file:Suppress("TooManyFunctions")

package dev.jamesyox.kastro.util

import dev.drewhamilton.poko.Poko
import kotlin.math.cos
import kotlin.math.sin

/**
 * A three-dimensional matrix.
 */
@Suppress("LongParameterList")
@Poko
internal class Matrix(
    @Poko.ReadArrayContent private val mx: DoubleArray
) {
    constructor(
        d1: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double,
        d6: Double,
        d7: Double,
        d8: Double,
        d9: Double
    ) : this(doubleArrayOf(d1, d2, d3, d4, d5, d6, d7, d8, d9))

    constructor() : this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    /**
     * Transposes this matrix.
     *
     * @return [Matrix] that is a transposition of this matrix.
     */
    fun transpose(): Matrix {
        val result = Matrix()
        for (i in 0..2) {
            for (j in 0..2) {
                result[i, j] = get(j, i)
            }
        }
        return result
    }

    /**
     * Negates this matrix.
     *
     * @return [Matrix] that is a negation of this matrix.
     */
    fun negate(): Matrix {
        val result = Matrix()
        for (i in 0..8) {
            result.mx[i] = -mx[i]
        }
        return result
    }

    /**
     * Adds a matrix to this matrix.
     *
     * @param right
     * [Matrix] to add
     * @return [Matrix] that is a sum of both matrices
     */
    operator fun plus(right: Matrix): Matrix {
        val result = Matrix()
        for (i in 0..8) {
            result.mx[i] = mx[i] + right.mx[i]
        }
        return result
    }

    /**
     * Subtracts a matrix from this matrix.
     *
     * @param right
     * [Matrix] to subtract
     * @return [Matrix] that is the difference of both matrices
     */
    operator fun minus(right: Matrix): Matrix {
        val result = Matrix()
        for (i in 0..8) {
            result.mx[i] = mx[i] - right.mx[i]
        }
        return result
    }

    /**
     * Multiplies two matrices.
     *
     * @param right
     * [Matrix] to multiply with
     * @return [Matrix] that is the product of both matrices
     */
    fun multiply(right: Matrix): Matrix {
        val result = Matrix()
        for (i in 0..2) {
            for (j in 0..2) {
                var scalp = 0.0
                for (k in 0..2) {
                    scalp += get(i, k) * right[k, j]
                }
                result[i, j] = scalp
            }
        }
        return result
    }

    /**
     * Performs a scalar multiplication.
     *
     * @param scalar
     * Scalar to multiply with
     * @return [Matrix] that is the scalar product
     */
    fun multiply(scalar: Double): Matrix {
        val result = Matrix()
        for (i in 0..8) {
            result.mx[i] = mx[i] * scalar
        }
        return result
    }

    /**
     * Applies this matrix to a [Vector].
     *
     * @param right
     * [Vector] to multiply with
     * @return [Vector] that is the product of this matrix and the given vector
     */
    fun multiply(right: Vector): Vector {
        val vec = doubleArrayOf(right.x, right.y, right.z)
        val result = DoubleArray(3)
        for (i in 0..2) {
            var scalp = 0.0
            for (j in 0..2) {
                scalp += get(i, j) * vec[j]
            }
            result[i] = scalp
        }
        return Vector(result[0], result[1], result[2])
    }

    /**
     * Gets a value from the matrix.
     *
     * @param r
     * Row number (0..2)
     * @param c
     * Column number (0..2)
     * @return Value at that position
     */
    operator fun get(r: Int, c: Int): Double {
        require(!(r < 0 || r > 2 || c < 0 || c > 2)) { "row/column out of range: $r:$c" }
        return mx[r * 3 + c]
    }

    /**
     * Changes a value in the matrix. As a [Matrix] object is immutable from the
     * outside, this method is private.
     *
     * @param r
     * Row number (0..2)
     * @param c
     * Column number (0..2)
     * @param v
     * New value
     */
    private operator fun set(r: Int, c: Int, v: Double) {
        require(!(r < 0 || r > 2 || c < 0 || c > 2)) { "row/column out of range: $r:$c" }
        mx[r * 3 + c] = v
    }

    companion object {
        /**
         * Creates an identity matrix.
         *
         * @return Identity [Matrix]
         */
        fun identity(): Matrix {
            return Matrix(
                1.0, 0.0, 0.0,
                0.0, 1.0, 0.0,
                0.0, 0.0, 1.0
            )
        }

        /**
         * Creates a matrix that rotates a vector by the given angle at the X axis.
         *
         * @param angle
         * angle, in radians
         * @return Rotation [Matrix]
         */
        fun rotateX(angle: Double): Matrix {
            val s = sin(angle)
            val c = cos(angle)
            return Matrix(
                1.0, 0.0, 0.0,
                0.0, c, s,
                0.0, -s, c
            )
        }

        /**
         * Creates a matrix that rotates a vector by the given angle at the Y axis.
         *
         * @param angle
         * angle, in radians
         * @return Rotation [Matrix]
         */
        fun rotateY(angle: Double): Matrix {
            val s = sin(angle)
            val c = cos(angle)
            return Matrix(
                c, 0.0, -s,
                0.0, 1.0, 0.0,
                s, 0.0, c
            )
        }

        /**
         * Creates a matrix that rotates a vector by the given angle at the Z axis.
         *
         * @param angle
         * angle, in radians
         * @return Rotation [Matrix]
         */
        fun rotateZ(angle: Double): Matrix {
            val s = sin(angle)
            val c = cos(angle)
            return Matrix(
                c, s, 0.0,
                -s, c, 0.0,
                0.0, 0.0, 1.0
            )
        }
    }
}
