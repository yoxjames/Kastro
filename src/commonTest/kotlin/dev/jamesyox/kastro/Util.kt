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

package dev.jamesyox.kastro

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Suppress("NOTHING_TO_INLINE")
internal inline fun assertSimilar(
    expected: Instant,
    actual: Instant,
    tolerance: Duration = 1.seconds
) {
    assertTrue(
        actual = (expected - actual).absoluteValue < tolerance,
        message = "Expected $expected Actual $actual tolerance $tolerance but they differ by ${(actual - expected).absoluteValue}")
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun assertSimilar(
    expected: Double,
    actual: Double,
    tolerance: Double = 0.1
) {
    assertEquals(
        expected = expected,
        actual = actual,
        absoluteTolerance = tolerance
    )
}