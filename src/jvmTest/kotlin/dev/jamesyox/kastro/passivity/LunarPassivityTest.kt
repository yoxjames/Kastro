/*
 * Copyright (C) 2026 James Yox
 *   http://www.jamesyox.dev
 * Copyright (C) 2017 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package dev.jamesyox.kastro.passivity

import dev.jamesyox.kastro.Locations.DENVER
import dev.jamesyox.kastro.Locations.DENVER_TZ
import dev.jamesyox.kastro.assertSimilar
import dev.jamesyox.kastro.luna.LunarEvent
import dev.jamesyox.kastro.luna.LunarEventSequence
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

class LunarPassivityTest {
    companion object {
        private val filePath = Path("testData/lunarEvents.dat")
    }

    // Unignore and run this one to generate passivity data. Then we can run our assertions against it.
    @Test
    @Ignore
    fun generateLunarEventsPassivityData() {
        // Picking out a random time
        val now = LocalDateTime(2026, 1, 1, 0, 0, 0).toInstant(DENVER_TZ)
        // Request every event for an arbitrary location (DENVER)
        val lunarEventSequence = LunarEventSequence(
            start = now,
            location = DENVER,
            requestedLunarEvents = LunarEvent.all,
            limit = Duration.INFINITE
        )
        // Get ourselves a file writer and spin through a massive sequence of lunar events, write them to the file.
        // Quick and dirty but gets the job done!
        filePath.bufferedWriter().use { writer ->
            lunarEventSequence.take(20_000).forEach {
                writer.appendLine(it.toDataString())
            }
        }
    }

    @Test
    fun assertAgainstLunarEventPassivityData() {
        // Picking out a random time
        val now = LocalDateTime(2026, 1, 1, 0, 0, 0).toInstant(DENVER_TZ)
        // Request every event for an arbitrary location (DENVER)
        val solarEventSequence = LunarEventSequence(
            start = now,
            location = DENVER,
            requestedLunarEvents = LunarEvent.all,
            limit = Duration.INFINITE
        )
        filePath.bufferedReader().use { reader ->
            solarEventSequence.take(20_000).forEach { event ->
                assertCloseEnough(expected = event, actual = reader.readLine())
            }
        }
    }

    private fun LunarEvent.toDataString(): String {
        return "${this::class.simpleName}-${time.epochSeconds}"
    }

    private fun assertCloseEnough(expected: LunarEvent, actual: String) {
        val components = actual.split("-")
        val name = components[0]
        val time = components[1].toLong()
        assertEquals(expected::class.simpleName, name)
        assertSimilar(expected.time.epochSeconds.toDouble(), time.toDouble(), tolerance = 1.0)
    }
}
