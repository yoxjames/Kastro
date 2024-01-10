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

import dev.jamesyox.kastro.Locations.DENVER
import dev.jamesyox.kastro.Locations.DENVER_TZ
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn

class LunarEventsSequenceTest {
    @Test
    fun testAllLunarEvents() {
        val iter = LunarEventSequence(
            start = LocalDate(2023, 8, 1).atStartOfDayIn(DENVER_TZ),
            location = DENVER,
            limit = 8.days
        ).iterator()

        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 1, 5, 26),
            timeZone = DENVER_TZ,
        )
        iter.assertSimilar<LunarEvent.PhaseEvent.FullMoon>(
            expected = LocalDateTime(2023, 8, 1, 12, 31),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 1, 20, 47),
            timeZone = DENVER_TZ,
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 2, 6, 48),
            timeZone = DENVER_TZ,
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 2, 21, 23),
            timeZone = DENVER_TZ,
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 3, 8, 9),
            timeZone = DENVER_TZ,
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 3, 21, 52),
            timeZone = DENVER_TZ,
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 4, 9, 26),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 4, 22, 19),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 5, 10, 40),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 5, 22, 43),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 6, 11, 52),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 6, 23, 9),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 7, 13, 3),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 8, 7, 23, 36),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.PhaseEvent.LastQuarter>(
            expected = LocalDateTime(2023, 8, 8, 4, 28),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 8, 8, 14, 12),
            timeZone = DENVER_TZ,
            tolerance = 2.minutes
        )
        assertFalse(iter.hasNext())
    }

    @Test
    fun testSubsetOfEvents() {
        val iter = LunarEventSequence(
            start = LocalDate(2023, 10, 26).atStartOfDayIn(DENVER_TZ),
            location = DENVER,
        ).iterator()

        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 10, 26, 4, 39),
            timeZone = DENVER_TZ,
            tolerance = 1.minutes
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 10, 26, 17, 6),
            timeZone = DENVER_TZ,
            tolerance = 1.minutes
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 10, 27, 5, 53),
            timeZone = DENVER_TZ,
            tolerance = 1.minutes
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonrise>(
            expected = LocalDateTime(2023, 10, 27, 17, 31),
            timeZone = DENVER_TZ,
            tolerance = 1.minutes
        )
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 10, 28, 7, 8),
            timeZone = DENVER_TZ,
            tolerance = 1.minutes
        )
        iter.assertSimilar<LunarEvent.PhaseEvent.FullMoon>(
            expected = LocalDateTime(2023, 10, 28, 14, 24),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        repeat(16) { iter.next() }
        iter.assertSimilar<LunarEvent.HorizonEvent.Moonset>(
            expected = LocalDateTime(2023, 11, 5, 13, 40),
            timeZone = DENVER_TZ,
            tolerance = 1.minutes
        )
    }

    @Test
    fun testEmptyEventTypes() {
        val iter = LunarEventSequence(
            start = LocalDate(2023, 8, 1).atStartOfDayIn(DENVER_TZ),
            location = DENVER,
            requestedLunarEvents = emptyList()
        ).iterator()

        assertFalse(iter.hasNext())
    }
}