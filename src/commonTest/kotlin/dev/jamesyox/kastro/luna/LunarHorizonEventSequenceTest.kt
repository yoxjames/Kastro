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

import dev.jamesyox.kastro.Locations.ALERT
import dev.jamesyox.kastro.Locations.COLOGNE
import dev.jamesyox.kastro.Locations.DENVER
import dev.jamesyox.kastro.Locations.DENVER_TZ
import dev.jamesyox.kastro.Locations.PUERTO_WILLIAMS
import dev.jamesyox.kastro.Locations.SINGAPORE
import dev.jamesyox.kastro.Locations.WELLINGTON
import dev.jamesyox.kastro.assertSimilar
import dev.jamesyox.kastro.luna.LunarEvent.HorizonEvent.Moonrise
import dev.jamesyox.kastro.luna.LunarEvent.HorizonEvent.Moonset
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.TimeZone

class LunarHorizonEventSequenceTest {
    @Test
    fun testCologne() {
        LunarHorizonEventSequence(
            start = LocalDate(year = 2017, month = Month.JULY, dayOfMonth = 12).atStartOfDayIn(UTC),
            location = COLOGNE
        ).assertEvents {
            assertEvent<Moonset>(
                LocalDateTime(year = 2017, month = Month.JULY, dayOfMonth = 12, hour = 6, minute = 53, second = 30)
            )
            assertEvent<Moonrise>(
                LocalDateTime(year = 2017, month = Month.JULY, dayOfMonth = 12, hour = 21, minute = 25, second = 55)
            )
        }
    }

    @Test
    fun testAlert() {
        LunarHorizonEventSequence(
            start = LocalDate(year = 2017, month = Month.JULY, dayOfMonth = 12).atStartOfDayIn(UTC),
            location = ALERT,
            limit = 1.days
        ).assertEmpty()

        LunarHorizonEventSequence(
            start = LocalDate(year = 2017, month = Month.JULY, dayOfMonth = 12).atStartOfDayIn(UTC),
            location = ALERT
        ).assertEvents {
            assertEvent<Moonrise>(LocalDateTime(2017, 7, 14, 5, 45, 5))
            assertEvent<Moonset>(LocalDateTime(2017, 7, 14, 11, 26, 43))
        }

        LunarHorizonEventSequence(
            start = LocalDate(year = 2017, month = Month.JULY, dayOfMonth = 14).atStartOfDayIn(UTC),
            location = ALERT,
            limit = 1.days
        ).assertEvents {
            assertEvent<Moonrise>(LocalDateTime(2017, 7, 14, 5, 45, 5))
            assertEvent<Moonset>(LocalDateTime(2017, 7, 14, 11, 26, 43))
            assertFalse(hasNext())
        }

        LunarHorizonEventSequence(
            start =  LocalDate(2017, 7, 18).atStartOfDayIn(UTC),
            location = ALERT,
            limit = 1.days
        ).assertEmpty()

        LunarHorizonEventSequence(
            start = LocalDate(2017, 7, 18).atStartOfDayIn(UTC),
            location = ALERT
        ).assertEvents {
            assertEvent<Moonset>(LocalDateTime(2017, 7,27, 4, 7,10))
            assertEvent<Moonrise>(LocalDateTime(2017, 7, 27, 11,59, 20))
        }
    }

    @Test
    fun testWellington() {
        LunarHorizonEventSequence(
            start = LocalDate(2017, 7, 12).atStartOfDayIn(UTC),
            location = WELLINGTON
        ).assertEvents {
            assertEvent<Moonrise>(LocalDateTime(2017, 7, 12, 8, 5, 53))
            assertEvent<Moonset>(LocalDateTime(2017, 7, 12, 21, 57, 38))
        }
    }

    @Test
    fun testPuertoWilliams() {
        LunarHorizonEventSequence(
            start = LocalDate(2017, 7, 13).atStartOfDayIn(UTC),
            location = PUERTO_WILLIAMS
        ).assertEvents {
            assertEvent<Moonrise>(LocalDateTime(2017, 7, 13, 0, 31, 29))
            assertEvent<Moonset>(LocalDateTime(2017, 7, 13, 14, 48, 37))
        }
    }

    @Test
    fun testSingapore() {
        LunarHorizonEventSequence(
            start = LocalDate(2017, 7, 13).atStartOfDayIn(UTC),
            location = SINGAPORE
        ).assertEvents {
            assertEvent<Moonset>(LocalDateTime(2017, 7, 13, 2, 8, 57))
            assertEvent<Moonrise>(LocalDateTime(2017, 7, 13, 14, 35, 9))
        }
    }

    @Test
    fun testSequence() {
        val riseBefore = LocalDateTime(2017, 11, 25, 12, 0).toInstant(UTC)
        val riseAfter = LocalDateTime(2017, 11, 26, 12, 29).toInstant(UTC)
        val setBefore = LocalDateTime(2017, 11, 25, 21, 49).toInstant(UTC)
        val setAfter = LocalDateTime(2017, 11, 26, 22, 55).toInstant(UTC)
        for (hour in 0..23) {
            for (minute in 0..59) {
                val result = LunarHorizonEventSequence(
                    start = LocalDateTime(2017, 11, 25, hour, minute, 0).toInstant(UTC),
                    location = COLOGNE,
                )
                val rise = result.first { it is Moonrise }.time
                val set = result.first { it is Moonset }.time
                if (hour < 12 || hour == 12 && minute == 0) {
                    assertSimilar(riseBefore, rise, 1.minutes)
                } else {
                    assertSimilar(riseAfter, rise, 1.minutes)
                }
                if (hour < 21 || hour == 21 && minute <= 49) {
                    assertSimilar(setBefore, set, 1.minutes)
                } else {
                    assertSimilar(setAfter, set, 1.minutes)
                }
            }
        }
    }

    @Test
    fun testDenver() {
        LunarHorizonEventSequence(
            start = LocalDate(2023, 12, 15).atStartOfDayIn(DENVER_TZ),
            location = DENVER
        ).assertEvents {
            assertEvent<Moonrise>(
                expected = LocalDateTime(2023, 12, 15, 10, 7),
                timeZone = DENVER_TZ,
                tolerance = 1.minutes
            )
            assertEvent<Moonset>(
                expected = LocalDateTime(2023, 12, 15, 19, 30),
                timeZone = DENVER_TZ,
                tolerance = 1.minutes
            )
            assertEvent<Moonrise>(
                expected = LocalDateTime(2023, 12, 16, 10, 48),
                timeZone = DENVER_TZ,
                tolerance = 1.minutes
            )
            assertEvent<Moonset>(
                expected = LocalDateTime(2023, 12, 16, 20, 48),
                timeZone = DENVER_TZ,
                tolerance = 1.minutes
            )
        }
    }

    // This test tests a case where moon set and moon rise happen within the same hour
    @Test
    fun testFarNorth() {
        val location = Pair(64.616667, 20.6) // Järvtjärn, Sweden
        val warsawTz = TimeZone.of("Europe/Warsaw")

        LunarHorizonEventSequence(
            start = LocalDate(2023, 12, 1).atStartOfDayIn(warsawTz),
            location = location
        ).assertEvents {
            assertEvent<Moonset>(
                expected = LocalDateTime(2023, 12, 1, 14, 45),
                timeZone = warsawTz,
                tolerance = 2.minutes // Less precise but acceptable.
            )
            assertEvent<Moonrise>(
                expected = LocalDateTime(2023, 12, 1, 15, 36),
                timeZone = warsawTz,
                tolerance = 2.minutes // Less precise but acceptable.
            )
        }

        LunarHorizonEventSequence(
            start = LocalDate(2023, 1, 4).atStartOfDayIn(warsawTz),
            location = location
        ).take(50).toList().also { println(it) }

        LunarHorizonEventSequence(
            start = LocalDate(2023, 1, 4).atStartOfDayIn(warsawTz),
            location = location
        ).assertEvents {
            assertEvent<Moonset>(
                expected = LocalDateTime(2023, 1, 4, 9, 12),
                timeZone = warsawTz,
                tolerance = 1.minutes
            )
            assertEvent<Moonrise>(
                expected = LocalDateTime(2023, 1, 4, 9, 53),
                timeZone = warsawTz,
                tolerance = 1.minutes
            )
            assertEvent<Moonset>(
                expected = LocalDateTime(2023, 1, 8, 11, 36),
                timeZone = warsawTz,
                tolerance = 2.minutes
            )
            assertEvent<Moonrise>(
                expected = LocalDateTime(2023, 1, 8, 14, 32),
                timeZone = warsawTz,
                tolerance = 2.minutes
            )
            // Skip 5 days worth of moonset and moonrises
            next(); next()
            next(); next()
            next(); next()
            next(); next()
            next(); next()

            // We want to ensure we are still getting acceptable results
            assertEvent<Moonset>(
                expected = LocalDateTime(2023, 1, 14, 10, 27),
                timeZone = warsawTz,
                tolerance = 1.minutes
            )
        }
    }

    private fun Sequence<LunarEvent>.assertEmpty() {
        assertTrue(none())
    }

    private fun Sequence<LunarEvent.HorizonEvent>.assertEvents(block: Iterator<LunarEvent>.() -> Unit) {
        iterator().block()
    }

    private inline fun <reified T : LunarEvent> Iterator<LunarEvent>.assertEvent(
        expected: LocalDateTime,
        timeZone: TimeZone = UTC,
        tolerance: Duration = 1.seconds
    ) {
        next().let {
            assertIs<T>(it)
            assertSimilar(expected = expected.toInstant(timeZone), actual = it.time, tolerance = tolerance)
        }
    }
}