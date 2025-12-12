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

import dev.jamesyox.kastro.util.ExtendedMath.PI2
import dev.jamesyox.kastro.util.Moon
import dev.jamesyox.kastro.util.Pegasus.calculate
import dev.jamesyox.kastro.util.Sol
import dev.jamesyox.kastro.util.atJulianCentury
import dev.jamesyox.kastro.util.daysInJulianCentury
import dev.jamesyox.kastro.util.instant
import dev.jamesyox.kastro.util.julianDate
import dev.jamesyox.kastro.util.radians
import dev.jamesyox.kastro.util.sortedByReversible
import kotlin.math.PI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * [Sequence] representing all [LunarEvent.PhaseEvent]s supported by Kastro. The sequence will be ordered by
 * [LunarEvent.time] with times farthest in the future being later in the [Sequence]. As implied by being a [Sequence]
 *
 * As implied by being a [Sequence] values will be lazily calculated so setting a large or even infinite
 * limit is acceptable.
 *
 * @param start Time representing the beginning of the sequence
 * @param limit Point at which the sequence will end. If you wish for an infinite sequence simply put
 * [Duration.INFINITE] for limit. Calculations are done lazily so requesting an infinite sequence is not harmful.
 * Limit only exists to allow certain logical checks such as if you wanted to know if there will be any event in the
 * next hour you could use limit and then check if the resulting sequence is empty. Defaults to 365 days.
 * @param requestedLunarPhases List of [LunarPhase.Primary] events to be included in the sequence. Providing only
 * the phases you are interested in can save computation resources. Defaults to [LunarPhase.Primary.all].
 * @param reverse Whether the sequence should advance in reverse chronological order. By default, this is false
 */
public class LunarPhaseSequence(
    private val start: Instant,
    private val limit: Duration = 365.days,
    private val requestedLunarPhases: List<LunarPhase.Primary> = LunarPhase.Primary.all,
    private val reverse: Boolean = false,
) : Sequence<LunarEvent.PhaseEvent> {

    private companion object {
        private const val SUN_LIGHT_TIME_TAU = 8.32 / (1440.0 * daysInJulianCentury)
    }

    public override fun iterator(): Iterator<LunarEvent.PhaseEvent> {
        return generateSequence(calculateNextLunarPhaseEvents(start)) { moonPhaseEvents ->
            moonPhaseEvents.lastOrNull()?.let { last ->
                calculateNextLunarPhaseEvents(startingAt = last.time + if (reverse) (-500).seconds else 500.seconds)
            }
        }.flatMap { it.asSequence() }.iterator()
    }

    private fun calculateNextLunarPhaseEvents(startingAt: Instant): List<LunarEvent.PhaseEvent> {
        return requestedLunarPhases.mapNotNull {
            calculateNextLunarPhaseEvent(start = startingAt, phase = it)
        }.sortedByReversible(reverse) { it.time }
    }

    private fun calculateNextLunarPhaseEvent(
        start: Instant,
        phase: LunarPhase.Primary,
    ): LunarEvent.PhaseEvent? {
        val stepRate = 7.days
        val dT = stepRate.inWholeDays / daysInJulianCentury
        val accuracy = 0.5 / 1440.0 / daysInJulianCentury // accuracy: 30 seconds
        var t0 = (if (reverse) (start - stepRate) else start).julianDate.julianCentury
        var t1 = t0 + dT
        var d0 = lunarPhase(t0, phase.phase.radians)
        var d1 = lunarPhase(t1, phase.phase.radians)
        while (d0 * d1 > 0.0 || d1 < d0) {
            when (reverse) {
                true -> {
                    t1 = t0
                    d1 = d0
                    t0 -= dT
                    d0 = lunarPhase(t0, phase.phase.radians)
                }
                false -> {
                    t0 = t1
                    d0 = d1
                    t1 += dT
                    d1 = lunarPhase(t1, phase.phase.radians)
                }
            }
        }
        val tPhase = calculate(t0, t1, accuracy) { lunarPhase(it, phase.phase.radians) }
        val tjd = atJulianCentury(tPhase)
        return if (tjd.instant <= (this.start + limit)) phase.phaseInformation(time = tjd.instant) else null
    }

    /*
     * Calculates the position of the moon at the given phase.
     *
     * @param ephemerisTime Ephemeris time
     * @return difference angle of the sun's and moon's position
     */
    private fun lunarPhase(ephemerisTime: Double, phase: Double): Double {
        val sun = Sol.positionEquatorial(atJulianCentury(ephemerisTime - SUN_LIGHT_TIME_TAU))
        val moon = Moon.positionEquatorial(atJulianCentury(ephemerisTime))
        var diff = moon.phi - sun.phi - phase
        while (diff < 0.0) {
            diff += PI2
        }
        return ((diff + PI) % PI2) - PI
    }
}
