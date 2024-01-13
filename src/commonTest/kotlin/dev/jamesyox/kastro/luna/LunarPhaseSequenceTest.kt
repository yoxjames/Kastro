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

import dev.jamesyox.kastro.Locations.DENVER_TZ
import dev.jamesyox.kastro.assertSimilar
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.NewMoon
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.FirstQuarter
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.FullMoon
import dev.jamesyox.kastro.luna.LunarEvent.PhaseEvent.LastQuarter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

private const val ERROR = 500.0

class LunarPhaseSequenceTest {
    @Test
    fun testNewMoon() {
        val mp = LunarPhaseSequence(
            start = LocalDate(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 1).atStartOfDayIn(UTC),
            requestedLunarPhases = listOf(NewMoon)
        ).first()

        assertSimilar(
            actual = mp.time,
            expected = LocalDateTime(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 20, hour = 5, minute = 29, second = 30).toInstant(UTC),
        )
        assertEquals(expected = 382740.0, actual = mp.time.calculateLunarDistance(), absoluteTolerance = ERROR)
    }

    @Test
    fun testFirstQuarterMoon() {
        val mp = LunarPhaseSequence(
            start = LocalDate(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 1).atStartOfDayIn(UTC),
            requestedLunarPhases = listOf(FirstQuarter)
        ).first()

        assertSimilar(
            actual = mp.time,
            expected = LocalDateTime(year = 2017, Month.SEPTEMBER, dayOfMonth = 28, hour = 2, minute = 52, second = 40).toInstant(UTC),
        )
        assertEquals(expected = 403894.0, actual = mp.time.calculateLunarDistance(), absoluteTolerance = ERROR)
    }

    @Test
    fun testFullMoon() {
        val mp = LunarPhaseSequence(
            start = LocalDate(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 1).atStartOfDayIn(UTC),
            requestedLunarPhases = listOf(FullMoon)
        ).first()

        assertSimilar(
            actual = mp.time,
            expected = LocalDateTime(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 6, hour = 7, minute = 7, second = 44).toInstant(UTC),
        )
        assertEquals(expected = 384364.0, actual = mp.time.calculateLunarDistance(), absoluteTolerance = ERROR)
    }

    @Test
    fun testLastQuarterMoon() {
        val mp = LunarPhaseSequence(
            start = LocalDate(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 1).atStartOfDayIn(UTC),
            requestedLunarPhases = listOf(LastQuarter)
        ).first()

        assertSimilar(
            actual = mp.time,
            expected = LocalDateTime(year = 2017, month = Month.SEPTEMBER, dayOfMonth = 13, hour = 6, minute = 28, second = 34).toInstant(UTC),
        )
        assertEquals(actual = mp.time.calculateLunarDistance(), expected = 369899.0, absoluteTolerance = ERROR)
    }

    @Test
    fun testTwoFullCycles() {
        val iter = LunarPhaseSequence(
            start = LocalDate(2023, month = Month.DECEMBER, dayOfMonth = 15).atStartOfDayIn(DENVER_TZ)
        ).iterator()

        iter.assertSimilar<FirstQuarter>(
            expected = LocalDateTime(2023, 12, 19, 11, 39),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<FullMoon>(
            expected = LocalDateTime(2023, 12, 26, 17, 33),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<LastQuarter>(
            expected = LocalDateTime(2024, 1, 3, 20, 30),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<NewMoon>(
            expected = LocalDateTime(2024, 1, 11, 4, 57),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<FirstQuarter>(
            expected = LocalDateTime(2024, 1, 17, 20, 52),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<FullMoon>(
            expected = LocalDateTime(2024, 1, 25, 10, 54),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
        iter.assertSimilar<LastQuarter>(
            expected = LocalDateTime(2024, 2, 2, 16, 18),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )
    }

    @Test
    fun testNoPhases() {
        val iter = LunarPhaseSequence(
            start = LocalDate(2023, month = Month.DECEMBER, dayOfMonth = 15).atStartOfDayIn(DENVER_TZ),
            limit = 3.days
        ).iterator()

        assertFalse(iter.hasNext())
    }

    @Test
    fun testLimit() {
        val iter = LunarPhaseSequence(
            start = LocalDate(2024, month = Month.JANUARY, dayOfMonth = 11).atStartOfDayIn(DENVER_TZ),
            limit = 7.days
        ).iterator()

        iter.assertSimilar<NewMoon>(
            expected = LocalDateTime(2024, month = Month.JANUARY, dayOfMonth = 11, hour = 4, minute = 57),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )

        iter.assertSimilar<FirstQuarter>(
            expected = LocalDateTime(2024, month = Month.JANUARY, dayOfMonth = 17, hour = 20, 52),
            timeZone = DENVER_TZ,
            tolerance = 5.minutes
        )

        assertFalse(iter.hasNext())
    }

}