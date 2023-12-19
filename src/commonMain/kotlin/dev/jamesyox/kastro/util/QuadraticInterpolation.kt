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

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Calculates the roots and extremum of a quadratic equation.
 */
internal class QuadraticInterpolation private constructor(
    /**
     * Returns X of extremum. Can be outside [-1 .. 1].
     *
     * @return X
     */
    val xe: Double,

    /**
     * Returns the Y value at the extremum.
     *
     * @return Y
     */
    val ye: Double,

    // Waiting for Kotlin feature to let me do this with a single val. Soon....
    @Suppress("ConstructorParameterNaming")
    private val _root1: Double = 0.0,

    /**
     * Returns the second root that was found.
     *
     * @return X of second root
     */
    val root2: Double = 0.0,

    /**
     * Returns the number of roots found in [-1 .. 1].
     *
     * @return Number of roots
     */
    val numberOfRoots: Int,

    /**
     * Returns whether the extremum is a minimum or a maximum.
     *
     * @return `true`: Extremum at xe is a maximum. `false`: Extremum at xe is
     * a minimum.
     */
    val isMaximum: Boolean
) {
    /**
     * Returns the first root that was found.
     *
     * @return X of first root
     */
    val root1 = if (_root1 < -1.0) root2 else _root1

    companion object {
        fun of(yMinus: Double, y0: Double, yPlus: Double): QuadraticInterpolation {
            val root1: Double
            val root2: Double
            val a = 0.5 * (yPlus + yMinus) - y0
            val b = 0.5 * (yPlus - yMinus)
            val xe = -b / (2.0 * a)
            val ye = (a * xe + b) * xe + y0
            val isMaximum = a < 0.0
            val dis = b * b - 4.0 * a * y0
            var rootCount = 0
            if (dis >= 0.0) {
                val dx = 0.5 * sqrt(dis) / abs(a)
                root1 = xe - dx
                root2 = xe + dx
                if (abs(root1) <= 1.0) {
                    rootCount++
                }
                if (abs(root2) <= 1.0) {
                    rootCount++
                }
            } else {
                root1 = Double.NaN
                root2 = Double.NaN
            }
            val numberOfRoots = rootCount

            return QuadraticInterpolation(xe, ye, root1, root2, numberOfRoots, isMaximum)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as QuadraticInterpolation

        if (xe != other.xe) return false
        if (ye != other.ye) return false
        if (_root1 != other._root1) return false
        if (root2 != other.root2) return false
        if (numberOfRoots != other.numberOfRoots) return false
        if (isMaximum != other.isMaximum) return false
        if (root1 != other.root1) return false

        return true
    }

    override fun hashCode(): Int {
        var result = xe.hashCode()
        result = 31 * result + ye.hashCode()
        result = 31 * result + _root1.hashCode()
        result = 31 * result + root2.hashCode()
        result = 31 * result + numberOfRoots
        result = 31 * result + isMaximum.hashCode()
        result = 31 * result + root1.hashCode()
        return result
    }

    override fun toString(): String {
        return "QuadraticInterpolation(" +
            "xe=$xe, " +
            "ye=$ye, " +
            "_root1=$_root1, " +
            "root2=$root2, " +
            "numberOfRoots=$numberOfRoots, " +
            "isMaximum=$isMaximum, " +
            "root1=$root1" +
            ")"
    }
}
