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

/**
 * Finds the root of a function by using the Pegasus method.
 *
 * @see [regula falsi](https://en.wikipedia.org/wiki/False_position_method)
 */
internal object Pegasus {
    private const val MAX_ITERATIONS = 30

    /**
     * Find the root of the given function within the boundaries.
     *
     * @param lower
     * Lower boundary
     * @param upper
     * Upper boundary
     * @param accuracy
     * Desired accuracy
     * @param f
     * Function to be used for calculation
     * @return root that was found
     * @throws ArithmeticException
     * if the root could not be found in the given accuracy within
     * {@value #MAX_ITERATIONS} iterations.
     */
    fun calculate(lower: Double, upper: Double, accuracy: Double, f: (Double) -> Double): Double {
        var x1 = lower
        var x2 = upper
        var f1 = f(x1)
        var f2 = f(x2)
        if (f1 * f2 >= 0.0) {
            throw ArithmeticException("No root within the given boundaries")
        }
        var i = MAX_ITERATIONS
        while (i-- > 0) {
            val x3 = x2 - f2 / ((f2 - f1) / (x2 - x1))
            val f3 = f(x3)
            if (f3 * f2 <= 0.0) {
                x1 = x2
                f1 = f2
                x2 = x3
                f2 = f3
            } else {
                f1 = f1 * f2 / (f2 + f3)
                x2 = x3
                f2 = f3
            }
            if (abs(x2 - x1) <= accuracy) {
                return if (abs(f1) < abs(f2)) x1 else x2
            }
        }
        throw ArithmeticException("Maximum number of iterations exceeded")
    }
}
