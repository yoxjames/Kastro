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

import dev.jamesyox.kastro.Locations.DENVER
import dev.jamesyox.kastro.Locations.DENVER_TZ
import dev.jamesyox.kastro.luna.LunarEvent
import dev.jamesyox.kastro.luna.LunarEventSequence
import dev.jamesyox.kastro.luna.LunarHorizonEventSequence
import dev.jamesyox.kastro.luna.LunarPhaseSequence
import dev.jamesyox.kastro.sol.SolarEvent
import dev.jamesyox.kastro.sol.SolarEventSequence
import dev.jamesyox.kastro.sol.calculateSolarState
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class ReadmeExamples {

    private val latitude = DENVER.first
    private val longitude = DENVER.second
    private val clock = Clock.System
    private val timeZone = DENVER_TZ

    @Test @Ignore
    fun WhatTimeDoesTheSunSetNext() {
        val nextSunset = SolarEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.Sunset) // Not required but makes calculations more efficient
        ).first() // This example is safe, but first() can throw on empty sequences!

        println(nextSunset)
    }

    @Test @Ignore
    fun WhatTimeDidTheSunLastSet() {
        val lastSunset = SolarEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.Sunset), // Not required but makes calculations more efficient,
            reverse = true // Sequence goes backwards in time
        ).first() // This example is safe, but first() can throw on empty sequences!

        println(lastSunset)
    }

    @Test @Ignore
    fun WhenIsSolarNoonOnThe13thOfDecember() {
        val solarNoon = SolarEventSequence(
            start = LocalDate(2023, 12, 13).atStartOfDayIn(timeZone),
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.Noon)
        ).first() // This example is safe, but first() can throw on empty sequences!

        println(solarNoon.time)
    }

    @Test @Ignore
    fun ShowMeAFullWeekOfSunriseAndSunsetTimes() {
        val fullWeek = SolarEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.Sunrise, SolarEvent.Sunset),
            limit = 7.days
        ).toList()

        println(fullWeek)
    }

    @Test @Ignore
    fun WhenDoesTheNextGoldenHourBeginAndEnd() {
        val goldenHour = SolarEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.GoldenHourDusk, SolarEvent.GoldenHourDusk)
        ).firstOrNull()

        println(goldenHour)
    }

    @Test @Ignore
    fun WhatIsTheStateOfTheSunRightNow() {
        val sunState = clock.now().calculateSolarState(
            latitude = latitude,
            longitude = longitude
        )

        println(sunState)
    }

    @Test @Ignore
    fun ShowMeSunriseTimesForEveryTuesdayForAYear() {
        val tuesdaySunrises = SolarEventSequence(
            start = clock.now(), // starting today
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.Sunrise),
            limit = 365.days // Omitting leap year shenanigans
        ).filter {
            it.time.toLocalDateTime(timeZone).dayOfWeek == DayOfWeek.TUESDAY
        }.toList()

        println(tuesdaySunrises)
    }

    @Test @Ignore
    fun DoesTheSunSetInTheNextHour() {
        val doesItSet = SolarEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedSolarEvents = listOf(SolarEvent.Sunrise),
            limit = 1.hours
        ).any() // Returns true if anything is in the sequence

        println(doesItSet)
    }

    @Test @Ignore
    fun WhenIsTheNextMoonrise() {
        val nextMoonrise = LunarHorizonEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedHorizonEvents = listOf(LunarEvent.HorizonEvent.Moonrise)
        ).first()

        println(nextMoonrise)
    }

    @Test @Ignore
    fun WhenWasTheLastMoonrise() {
        val lastMoonrise = LunarHorizonEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedHorizonEvents = listOf(LunarEvent.HorizonEvent.Moonrise),
            reverse = true
        ).first()

        println(lastMoonrise)
    }

    @Test @Ignore
    fun WhenIsTheNextFullMoon() {
        val nextFullMoon = LunarPhaseSequence(
            start = clock.now(),
            requestedLunarPhases = listOf(LunarEvent.PhaseEvent.FullMoon)
        ).first()

        println(nextFullMoon)
    }

    @Test @Ignore
    fun WhenWasTheLastFullMoon() {
        val lastFullMoon = LunarPhaseSequence(
            start = clock.now(),
            requestedLunarPhases = listOf(LunarEvent.PhaseEvent.FullMoon),
            reverse = true
        ).first()

        println(lastFullMoon)
    }

    @Test @Ignore
    fun ShowMeAListOfMoonrisesMoonsetsAndMoonPhasesForTheNext30Days() {
        val moonList = LunarEventSequence(
            start = clock.now(),
            latitude = latitude,
            longitude = longitude,
            requestedLunarEvents = LunarEvent.all, // Show us everything!
            limit = 30.days
        ).toList()

        println(moonList)
    }

    @Test @Ignore
    fun DoAnyFullMoonsHappenOnFridaysInTheNextYear() {
        val fridayFullMoon = LunarPhaseSequence(
            start = clock.now(),
            requestedLunarPhases = listOf(LunarEvent.PhaseEvent.FullMoon),
            limit = 365.days // Omitting leap year shenanigans
        ).filter { it.time.toLocalDateTime(timeZone).dayOfWeek == DayOfWeek.FRIDAY }
            .any()

        println(fridayFullMoon)
    }
}