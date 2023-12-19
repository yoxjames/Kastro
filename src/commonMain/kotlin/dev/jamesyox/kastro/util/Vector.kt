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

import dev.jamesyox.kastro.util.ExtendedMath.PI2
import dev.jamesyox.kastro.util.ExtendedMath.isZero
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A three-dimensional vector.
 */
internal data class Vector(
    /**
     * Returns the cartesian X coordinate.
     */
    val x: Double,

    /**
     * Returns the cartesian Y coordinate.
     */
    val y: Double,

    /**
     * Returns the cartesian Z coordinate.
     */
    val z: Double,
) {
    /**
     * Returns the azimuthal angle (φ) in radians.
     */
    val phi: Double get() {
        return (if (isZero(x) && isZero(y)) 0.0 else atan2(y, x))
            .let { if (it < 0.0) it + PI2 else it }
    }

    /**
     * Returns the polar angle (θ) in radians.
     */
    val theta: Double get() {
        val sqr = x * x + y * y
        return if (isZero(z) && isZero(sqr)) {
            0.0
        } else {
            atan2(z, sqrt(sqr))
        }
    }

    /**
     * Returns the polar radial distance (r).
     */
    val r: Double get() = sqrt(x * x + y * y + z * z)

    /**
     * Returns a [Vector] that is the sum of this [Vector] and the given
     * [Vector].
     *
     * @param vec
     * [Vector] to add
     * @return Resulting [Vector]
     */
    operator fun plus(vec: Vector): Vector = Vector(x + vec.x, y + vec.y, z + vec.z)

    /**
     * Returns a [Vector] that is the difference of this [Vector] and the
     * given [Vector].
     *
     * @param vec
     * [Vector] to subtract
     * @return Resulting [Vector]
     */
    operator fun minus(vec: Vector): Vector = Vector(x - vec.x, y - vec.y, z - vec.z)

    /**
     * Returns a [Vector] that is the scalar product of this [Vector] and the
     * given scalar.
     *
     * @param scalar
     * Scalar to multiply
     * @return Resulting [Vector]
     */
    operator fun times(scalar: Double): Vector = Vector(x * scalar, y * scalar, z * scalar)

    /**
     * Returns the negation of this [Vector].
     *
     * @return Resulting [Vector]
     */
    operator fun unaryMinus(): Vector = Vector(-x, -y, -z)

    /**
     * Returns a [Vector] that is the cross product of this [Vector] and the
     * given [Vector].
     *
     * @param right
     * [Vector] to multiply
     * @return Resulting [Vector]
     */
    fun cross(right: Vector): Vector {
        return Vector(
            y * right.z - z * right.y,
            z * right.x - x * right.z,
            x * right.y - y * right.x
        )
    }

    /**
     * Returns the dot product of this [Vector] and the given [Vector].
     *
     * @param right
     * [Vector] to multiply
     * @return Resulting dot product
     */
    fun dot(right: Vector): Double = x * right.x + y * right.y + z * right.z

    /**
     * Returns the norm of this [Vector].
     *
     * @return Norm of this vector
     */
    fun norm(): Double = sqrt(dot(this))

    companion object {
        /**
         * Creates a new [Vector] of the given polar coordinates, with a radial distance
         * of 1.
         *
         * @param phi Azimuthal Angle
         * @param theta Polar Angle
         * @param r Radial Distance
         * @return Created [Vector]
         */
        fun ofPolar(phi: Double, theta: Double, r: Double = 1.0): Vector {
            val cosTheta = cos(theta)
            return Vector(
                r * cos(phi) * cosTheta,
                r * sin(phi) * cosTheta,
                r * sin(theta)
            )
        }
    }
}
