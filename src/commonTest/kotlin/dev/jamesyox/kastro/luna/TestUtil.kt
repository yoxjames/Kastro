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

package dev.jamesyox.kastro.luna

import dev.jamesyox.kastro.assertSimilar
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant

internal inline fun <reified T : LunarEvent>Iterator<LunarEvent>.assertSimilar(
    expected: LocalDateTime,
    timeZone: TimeZone = UTC,
    tolerance: Duration = 1.minutes
) {
    next().let {
        assertIs<T>(it)
        assertSimilar(expected = expected.toInstant(timeZone), actual = it.time, tolerance = tolerance)
    }
}
