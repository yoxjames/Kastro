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
package dev.jamesyox.kastro.sol

import dev.jamesyox.kastro.Locations.ALERT
import dev.jamesyox.kastro.Locations.COLOGNE
import dev.jamesyox.kastro.Locations.DENVER
import dev.jamesyox.kastro.Locations.DENVER_TZ
import dev.jamesyox.kastro.Locations.MARTINIQUE
import dev.jamesyox.kastro.Locations.MARTINIQUE_TZ
import dev.jamesyox.kastro.Locations.PUERTO_WILLIAMS
import dev.jamesyox.kastro.Locations.PUERTO_WILLIAMS_TZ
import dev.jamesyox.kastro.Locations.SANTA_MONICA
import dev.jamesyox.kastro.Locations.SANTA_MONICA_TZ
import dev.jamesyox.kastro.Locations.SINGAPORE
import dev.jamesyox.kastro.Locations.SINGAPORE_TZ
import dev.jamesyox.kastro.Locations.SYDNEY
import dev.jamesyox.kastro.Locations.SYDNEY_TZ
import dev.jamesyox.kastro.Locations.WELLINGTON
import dev.jamesyox.kastro.Locations.WELLINGTON_TZ
import dev.jamesyox.kastro.assertSimilar
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SolarEventSequenceTest {
    @Test
    fun testCologne() {
        val expectedEvents = listOf(
            SolarEvent.AstronomicalDawn(
                LocalDateTime(2017, 8, 10, 1, 44, 18).toInstant(UTC)
            ),
            SolarEvent.NauticalDawn(
                LocalDateTime(2017, 8, 10, 2, 44, 57).toInstant(UTC)
            ),
            SolarEvent.BlueHourDawn(
                LocalDateTime(2017, 8, 10, 3, 18, 22).toInstant(UTC)
            ),
            SolarEvent.CivilDawn(
                LocalDateTime(2017, 8, 10, 3, 34, 1).toInstant(UTC)
            ),
            SolarEvent.GoldenHourDawn(
                LocalDateTime(2017, 8, 10, 3, 34, 1).toInstant(UTC),
            ),
            SolarEvent.BlueHourDawnEnd(
                LocalDateTime(2017, 8, 10, 3, 48, 59).toInstant(UTC),
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 8, 10, 4, 11, 49).toInstant(UTC)
            ),
            SolarEvent.SunriseEnd(
                LocalDateTime(2017, 8, 10, 4, 15, 33).toInstant(UTC)
            ),
            SolarEvent.GoldenHourDawnEnd(
                LocalDateTime(2017, 8, 10, 4, 58, 33).toInstant(UTC)
            ),
            SolarEvent.Day(
                LocalDateTime(2017, 8, 10, 4, 17, 44).toInstant(UTC)
            ),
            SolarEvent.GoldenHourDusk(
                LocalDateTime(2017, 8, 10, 18, 15, 49).toInstant(UTC)
            ),
            SolarEvent.CivilDusk(
                LocalDateTime(2017, 8, 10, 18, 56, 30).toInstant(UTC)
            ),
            SolarEvent.SunsetBegin(
                LocalDateTime(2017, 8, 10, 18, 58, 39).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2017, 8, 10, 19, 2, 20).toInstant(UTC)
            ),
            SolarEvent.BlueHourDusk(
                LocalDateTime(2017, 8, 10, 19, 25, 16).toInstant(UTC)
            ),
            SolarEvent.NauticalDusk(
                LocalDateTime(2017, 8, 10, 19, 40, 13).toInstant(UTC)
            ),
            SolarEvent.GoldenHourDuskEnd(
                LocalDateTime(2017, 8, 10, 19, 40, 13).toInstant(UTC)
            ),
            SolarEvent.BlueHourDuskEnd(
                LocalDateTime(2017, 8, 10, 19, 55, 35).toInstant(UTC)
            ),
            SolarEvent.AstronomicalDusk(
                LocalDateTime(2017, 8, 10, 20, 28, 56).toInstant(UTC)
            ),
            SolarEvent.Night(
                LocalDateTime(2017, 8, 10, 21, 28, 43).toInstant(UTC)
            )
        ).sortedBy { it.time }

        val actual = SolarEventSequence(
            start = LocalDate(year = 2017, month = Month.AUGUST, dayOfMonth = 10).atStartOfDayIn(UTC),
            location = COLOGNE,
            requestedSolarEvents = SolarEventType.all
        ).filterNot { it is SolarEvent.Noon || it is SolarEvent.Nadir }
            .take(expectedEvents.size)
            .toList()

        assertSimilar(expectedEvents, actual)
    }

    private fun assertSimilar(expected: SolarEvent, actual: SolarEvent) {
        assertEquals(expected::class, actual::class)
        assertSimilar(expected.time, actual.time)
    }

    private fun assertSimilar(expected: List<SolarEvent>, actual: List<SolarEvent>) {
        assertEquals(expected.size, actual.size)
        expected.forEachIndexed { index, it ->
            assertSimilar(it, actual[index])
        }
    }

    @Test
    fun testAlert() {
        val t1 = SolarEventSequence(
            start = LocalDate(2017, 8, 10).atStartOfDayIn(UTC),
            location = ALERT,
        ).take(2).toList()
        val t1Expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 8, 10, 4, 16, 8).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 8, 10, 16, 13, 14).toInstant(UTC)
            ),
        )
        assertSimilar(t1Expected, t1)

        val t2 = SolarEventSequence(
            start = LocalDate(2017, 9, 24).atStartOfDayIn(UTC),
            location = ALERT,
        ).take(4).toList()
        val t2Expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 9, 24, 4, 3, 14).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 9, 24, 9, 54, 29).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 9, 24, 15, 59, 16).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2017, 9, 24, 22, 2, 1).toInstant(UTC)
            ),
        )
        assertSimilar(t2Expected, t2)

        val t3 = SolarEventSequence(
            start = LocalDate(2017, 2, 10).atStartOfDayIn(UTC),
            location = ALERT,
        ).take(2).toList()
        val t3Expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 2, 10, 4, 21, 58).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 2, 10, 16, 25, 9).toInstant(UTC)
            )
        )
        assertSimilar(t3Expected, t3)

        val t4seq = SolarEventSequence(
            LocalDate(2017, 8, 10).atStartOfDayIn(UTC),
            location = ALERT,
        )
        val t4ExpectedNoonNadir = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 8, 10, 4, 16, 9).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 8, 10, 16, 13, 14).toInstant(UTC)
            )
        )

        assertSimilar(t4ExpectedNoonNadir, t4seq.take(2).toList())

        val t4ExpectedSunriseSunset = listOf(
            SolarEvent.Sunset(
                LocalDateTime(2017, 9, 6, 3, 6, 2).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 9, 6, 5, 13, 15).toInstant(UTC)
            ),
        )

        assertSimilar(t4ExpectedSunriseSunset, t4seq.filterIsInstance<SolarEvent.HorizonEvent>().take(2).toList())


        val t5 = SolarEventSequence(
            start = LocalDate(2017, 2, 10).atStartOfDayIn(UTC),
            location = ALERT,
        )

        val t5ExpectedNoonNadir = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 2, 10, 4, 21, 59).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 2, 10, 16, 25, 9).toInstant(UTC)
            )
        )

        assertSimilar(
            expected = t5ExpectedNoonNadir,
            actual = t5.take(2).toList()
        )

        val t5ExpectedSunriseSunset = listOf(
            SolarEvent.Sunrise(
                LocalDateTime(2017, 2, 27, 15, 24, 18).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2017, 2, 27, 17, 23, 46).toInstant(UTC)
            )
        )

        assertSimilar(
            expected = t5ExpectedSunriseSunset,
            actual = t5.filterIsInstance<SolarEvent.HorizonEvent>().take(2).toList()
        )


        val t6 = SolarEventSequence(
            start = LocalDate(2017, 9, 6).atStartOfDayIn(UTC),
            location = ALERT,
        ).take(4).toList()
        val t6Expected = listOf(
            SolarEvent.Sunset(
                LocalDateTime(2017, 9, 6, 3, 6, 2).toInstant(UTC)
            ),
            SolarEvent.Nadir(
                LocalDateTime(2017, 9, 6, 4, 9, 31).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 9, 6, 5, 13, 15).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 9, 6, 16, 5, 41).toInstant(UTC)
            )
        )
        assertSimilar(t6Expected, t6)
        // Summer solstice is the worst case for noon calculation
        val t7 = SolarEventSequence(
            start = LocalDate(2020, 6, 20).atStartOfDayIn(UTC),
            location = ALERT,
        ).take(4).toList()
        val t7Expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2020, 6, 20, 4, 10, 54).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2020, 6, 20, 16, 11, 2).toInstant(UTC)
            ),
            SolarEvent.Nadir(
                LocalDateTime(2020, 6, 21, 4, 11, 9).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2020, 6, 21, 16, 11, 13).toInstant(UTC)
            )
        )
        assertSimilar(t7Expected, t7)
    }

    @Test
    fun testWellington() {
        val actual = SolarEventSequence(
            start = LocalDate(2017, 8, 10).atStartOfDayIn(WELLINGTON_TZ),
            location = WELLINGTON,
        ).take(4).toList()
        val expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 8, 9, 12, 26, 18).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 8, 9, 19, 18, 33).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 8, 10, 0, 26,33).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2017, 8, 10, 5, 34, 50).toInstant(UTC)
            )
        )
        assertSimilar(expected, actual)
    }


    @Test
    fun testPuertoWilliams() {
        val actual = SolarEventSequence(
            start = LocalDate(2017, 8, 10).atStartOfDayIn(PUERTO_WILLIAMS_TZ),
            location = PUERTO_WILLIAMS,
        ).take(4).toList()
        val expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 8, 10, 4, 35, 40).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 8, 10, 12, 1, 51).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 8, 10, 16, 36, 7).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2017, 8, 10, 21, 10, 36).toInstant(UTC)
            )
        )
        assertSimilar(expected, actual)
    }


    @Test
    fun testSingapore() {
        val actual = SolarEventSequence(
            start = LocalDate(2017, 8, 10).atStartOfDayIn(SINGAPORE_TZ),
            location = SINGAPORE,
        ).take(4).toList()
        val expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2017, 8, 9, 17, 10, 13).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2017, 8, 9, 23, 5, 13).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2017, 8, 10, 5, 10, 7).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2017, 8, 10, 11, 14, 56).toInstant(UTC)
            )
        )
        assertSimilar(expected, actual)
    }

    @Test
    fun testMartinique() {
        val actual = SolarEventSequence(
            start = LocalDate(2019, 7, 1).atStartOfDayIn(MARTINIQUE_TZ),
            location = MARTINIQUE,
        ).take(4).toList()
        val expected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2019, 7, 1, 4, 7, 52).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2019, 7, 1, 9, 38,35).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2019, 7, 1, 16, 7, 57).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2019, 7, 1, 22, 37, 23).toInstant(UTC)
            ),
        )
        assertSimilar(expected, actual)
    }
    
    @Test
    fun testSydney() {
        val actual = SolarEventSequence(
            start = LocalDate(2019, 7, 3).atStartOfDayIn(SYDNEY_TZ),
            location = SYDNEY,
        ).take(4).toList()
        val expected = listOf(
            SolarEvent.Sunrise(
                LocalDateTime(2019, 7, 2, 21, 0,35).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2019, 7, 3, 1, 59, 18).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2019, 7, 3, 6, 58, 2).toInstant(UTC)
            ),
            SolarEvent.Nadir(
                LocalDateTime(2019, 7, 3, 13, 59, 20).toInstant(UTC)
            )
        )
        assertSimilar(expected, actual)
    }
    

    /* TODO: These fail because the height calculation is incorrect. It was removed from the public API for now.
             eventually I hope to figure out the problem and fix it and restore height to the public API.
    @Test
    fun testHeight() {
        // At the top of the Tokyo Skytree
        val skytreeActual = SolarEventSequence(
            start = LocalDate(year = 2020, monthNumber = 6, dayOfMonth = 25)
                .atStartOfDayIn(TimeZone.of("Asia/Tokyo")),
            latitude = 35.710046,
            longitude = 139.810718,
            //height = 634.0
        ).take(4).toList()
        val skytreeExpected = listOf(
            SolarEvent.Sunrise(
                LocalDateTime(2020, 6, 24, 19, 21, 46).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2020, 6, 25, 2, 43, 28).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2020, 6, 25, 10,5, 17).toInstant(UTC)
            ),
            SolarEvent.Nadir(
                LocalDateTime(2020, 6, 25, 14, 43, 36).toInstant(UTC)
            )
        )
        assertSimilar(skytreeExpected, skytreeActual)

        // In an airplane at 38,000 feet
        val airplaneActual = SolarEventSequence(
            start = LocalDate(year = 2020, monthNumber = 6, dayOfMonth = 25).atStartOfDayIn(UTC),
            latitude = 46.58,
            longitude = -6.3,
            //height = 11582.4
        ).take(4).toList()
        val airplaneExpected = listOf(
            SolarEvent.Nadir(
                LocalDateTime(2020, 6, 25, 0, 27, 55).toInstant(UTC)
            ),
            SolarEvent.Sunrise(
                LocalDateTime(2020, 6, 25, 4, 7, 33).toInstant(UTC)
            ),
            SolarEvent.Noon(
                LocalDateTime(2020, 6, 25, 12, 28, 0).toInstant(UTC)
            ),
            SolarEvent.Sunset(
                LocalDateTime(2020, 6, 25, 20, 48, 32).toInstant(UTC)
            )
        )
        assertSimilar(airplaneExpected, airplaneActual)
    }
    */

    @Test
    fun testJustBeforeJustAfter() {
        // Thanks to @isomeme for providing the test cases for issue #18.
        val shortDuration = 2.minutes
        val longDuration = 30.minutes
        val date = LocalDate(2020, 5, 3)
        val acceptableError = 65.seconds
        
        val noon = SolarEventSequence(
            start = date.atStartOfDayIn(SANTA_MONICA_TZ),
            location = SANTA_MONICA,
        ).filterIsInstance<SolarEvent.Noon>()
            .first()
            .time
        
        val noonNextDay = SolarEventSequence(
            start = LocalDate(2020, 5, 4).atStartOfDayIn(SANTA_MONICA_TZ),
            location = SANTA_MONICA
        ).filterIsInstance<SolarEvent.Noon>()
            .first()
            .time

        val wellBeforeNoon = SolarEventSequence(
            start = noon - longDuration,
            location = SANTA_MONICA,
        ).filterIsInstance<SolarEvent.Noon>()
            .first()
            .time


        assertSimilar(wellBeforeNoon, noon, acceptableError)
        
        val justBeforeNoon = SolarEventSequence(
            start = noon - shortDuration,
            location = SANTA_MONICA,
        ).filterIsInstance<SolarEvent.Noon>()
            .first()
            .time

        assertSimilar(justBeforeNoon, noon, acceptableError)
        
        val justAfterNoon = SolarEventSequence(
            start = noon + shortDuration,
            location = SANTA_MONICA,
        ).filterIsInstance<SolarEvent.Noon>()
            .first()
            .time

        assertSimilar(justAfterNoon, noonNextDay, acceptableError)
        
        val wellAfterNoon = SolarEventSequence(
            start = noon + longDuration,
            location = SANTA_MONICA,
        ).filterIsInstance<SolarEvent.Noon>()
            .first()
            .time

        assertSimilar(wellAfterNoon, noonNextDay, acceptableError)
        
        val nadirWellAfterNoon = SolarEventSequence(
            start = wellAfterNoon,
            location = SANTA_MONICA
        ).filterIsInstance<SolarEvent.Nadir>()
            .first()
            .time
        
        val nadirJustBeforeNadir = SolarEventSequence(
            start = nadirWellAfterNoon - shortDuration,
            location = SANTA_MONICA
        ).filterIsInstance<SolarEvent.Nadir>()
            .first()
            .time

        assertSimilar(nadirWellAfterNoon, nadirJustBeforeNadir, acceptableError)
    }

    @Test
    fun testNoonNadirAzimuth() {
        // Thanks to @isomeme for providing the test cases for issue #20.
        assertNoonNadirPrecision(LocalDateTime(2020, 6, 2, 3, 30, 0, 0).toInstant(SANTA_MONICA_TZ), SANTA_MONICA)
        assertNoonNadirPrecision(LocalDateTime(2020, 6, 16, 4, 11, 0, 0).toInstant(SANTA_MONICA_TZ), SANTA_MONICA)
    }

    @Test
    fun testSequence() {
        val acceptableError = 62.seconds
        val riseBefore = LocalDateTime(2017, 11, 25, 7, 4).toInstant(UTC)
        val riseAfter = LocalDateTime(2017, 11, 26, 7, 6).toInstant(UTC)
        val setBefore = LocalDateTime(2017, 11, 25, 15, 33).toInstant(UTC)
        val setAfter = LocalDateTime(2017, 11, 26, 15, 32).toInstant(UTC)
        for (hour in 0..23) {
            for (minute in 0..59) {
                val times = SolarEventSequence(
                    start = LocalDateTime(2017, 11, 25, hour, minute, 0).toInstant(UTC),
                    location = COLOGNE,
                    limit = 1.days + 1.minutes
                )
                val rise = times.filterIsInstance<SolarEvent.Sunrise>().first()
                val set = times.filterIsInstance<SolarEvent.Sunset>().first()
                if (hour < 7 || hour == 7 && minute <= 4) {
                    assertSimilar(
                        expected = riseBefore,
                        actual = rise.time,
                        tolerance = acceptableError
                    )
                } else {
                    assertSimilar(
                        expected = riseAfter,
                        actual = rise.time,
                        tolerance = acceptableError
                    )
                }
                if (hour < 15 || hour == 15 && minute <= 33) {
                    assertSimilar(
                        expected = setBefore,
                        actual = set.time,
                        tolerance = acceptableError
                    )
                } else {
                    assertSimilar(
                        expected = setAfter,
                        actual = set.time,
                        tolerance = acceptableError
                    )
                }
            }
        }
    }

    @Test
    fun testDenver() {
        val iter = SolarEventSequence(
            start = LocalDate(2023, 12, 1).atStartOfDayIn(DENVER_TZ),
            location = DENVER,
            limit = 3.days,
            requestedSolarEvents = SolarEventType.simple - SolarEvent.Nadir
        ).iterator()

        iter.assertSimilar<SolarEvent.Sunrise>(
            expected = LocalDateTime(2023, 12, 1, 7, 1),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Noon>(
            expected = LocalDateTime(2023, 12, 1, 11, 48),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Sunset>(
            expected = LocalDateTime(2023, 12, 1, 16, 35),
            timeZone = DENVER_TZ,
            tolerance = 2.minutes
        )
        iter.assertSimilar<SolarEvent.Sunrise>(
            expected = LocalDateTime(2023, 12, 2, 7, 2),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Noon>(
            expected = LocalDateTime(2023, 12, 2, 11, 49),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Sunset>(
            expected = LocalDateTime(2023, 12, 2, 16, 35),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Sunrise>(
            expected = LocalDateTime(2023, 12, 3, 7, 3),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Noon>(
            expected = LocalDateTime(2023, 12, 3, 11, 49),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Sunset>(
            expected = LocalDateTime(2023, 12, 3, 16, 35),
            timeZone = DENVER_TZ
        )

        assertFalse(iter.hasNext())
    }

    @Test
    fun testThatWeDoNotGoBeyondOneDayWithLimitSet() {
        val iter = SolarEventSequence(
            start = LocalDate(2024, 1, 11).atStartOfDayIn(DENVER_TZ),
            location = DENVER,
            limit = 1.days,
            requestedSolarEvents = SolarEventType.simple - SolarEvent.Nadir
        ).iterator()

        iter.assertSimilar<SolarEvent.Sunrise>(
            expected = LocalDateTime(2024, 1, 11, 7, 20),
            timeZone = DENVER_TZ
        )

        iter.assertSimilar<SolarEvent.Noon>(
            expected = LocalDateTime(2024, 1, 11, 12, 7, 57),
            timeZone = DENVER_TZ
        )

        iter.assertSimilar<SolarEvent.Sunset>(
            expected = LocalDateTime(2024, 1, 11, 16, 55, 25),
            timeZone = DENVER_TZ
        )

        assertFalse(iter.hasNext())
    }

    @Test
    fun testEmptyEventTypes() {
        val iter = SolarEventSequence(
            start = LocalDate(2023, 8, 1).atStartOfDayIn(DENVER_TZ),
            location = DENVER,
            requestedSolarEvents = emptyList()
        ).iterator()

        assertFalse(iter.hasNext())
    }

    @Test
    fun testSuperLowLatitude() {
        val seq = SolarEventSequence(
            start = LocalDate(2024, 2, 17).atStartOfDayIn(DENVER_TZ),
            location = Pair(-83.0, -105.0),
            requestedSolarEvents = SolarEventType.all
        )

        println(seq.map { Pair(it, it.time.toLocalDateTime(DENVER_TZ)) }.take(10).toList())

        val iter = seq.iterator()

        iter.assertSimilar<SolarEvent.Nadir>(
            expected = LocalDateTime(2024, 2, 17, 0, 15, 54),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.GoldenHourDawnEnd>(
            expected = LocalDateTime(2024, 2, 17, 2, 13, 5),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Noon>(
            expected = LocalDateTime(2024, 2, 17, 12, 12, 10),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.GoldenHourDusk>(
            expected = LocalDateTime(2024, 2, 17, 21, 57, 3),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Nadir>(
            expected = LocalDateTime(2024, 2, 18, 0, 15, 50),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.GoldenHourDawnEnd>(
            expected = LocalDateTime(2024, 2, 18, 2, 35, 10),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.Noon>(
            expected = LocalDateTime(2024, 2, 18, 12, 12, 9),
            timeZone = DENVER_TZ
        )
        iter.assertSimilar<SolarEvent.GoldenHourDusk>(
            expected = LocalDateTime(2024, 2, 18, 21, 37, 47),
            timeZone = DENVER_TZ
        )
    }

    private fun assertNoonNadirPrecision(time: Instant, location: Pair<Double, Double>) {
        val sunTimes = SolarEventSequence(
            start = time,
            location = location,
        )
        val sunPositionAtNoon = sunTimes.first { it is SolarEvent.Noon }.time.calculateSolarState(location)
        val sunPositionAtNadir = sunTimes.first { it is SolarEvent.Nadir }.time.calculateSolarState(location)
        assertTrue(abs(sunPositionAtNoon.azimuth - 180.0) < 0.1)
        assertTrue(abs(sunPositionAtNadir.azimuth - 360.0) < 0.1)
    }

    internal inline fun <reified T : SolarEvent>Iterator<SolarEvent>.assertSimilar(
        expected: LocalDateTime,
        timeZone: TimeZone = UTC,
        tolerance: Duration = 1.minutes
    ) {
        next().let {
            assertIs<T>(it)
            assertSimilar(expected = expected.toInstant(timeZone), actual = it.time, tolerance = tolerance)
        }
    }
}
