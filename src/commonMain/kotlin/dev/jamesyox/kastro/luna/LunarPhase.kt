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

import kotlinx.datetime.Instant
import kotlin.math.absoluteValue

/**
 * A [LunarPhase] is a specific shape of the moon's sunlit portion. There are [Primary] moon phases which are described
 * by [LunarPhase.Primary.phase] and [Intermediate] moon phases which are described by
 * [LunarPhase.Intermediate.angleRange]
 *
 * See [Lunar Phase](https://en.wikipedia.org/wiki/Lunar_phase) for more information
 */
public sealed interface LunarPhase {

    /**
     * A primary moon phase. There are only 4 primary moon phases, New Moon, First Quarter, Full Moon, and Last Quarter
     * These phases happen at a single instant in time rather than describing a range between states.
     */
    public sealed interface Primary : LunarPhase, LunarEvent.LunarEventType {

        public companion object {
            /**
             * List containing all the primary moon phases.
             */
            public val all: List<Primary> = listOf(
                LunarEvent.PhaseEvent.NewMoon,
                LunarEvent.PhaseEvent.FirstQuarter,
                LunarEvent.PhaseEvent.LastQuarter,
                LunarEvent.PhaseEvent.FullMoon
            )
        }

        /**
         * Moon phase angle, in degrees. Will be within the range of 0..<360.
         *
         * 0 represents [New Moon][LunarEvent.PhaseEvent.NewMoon.Companion]
         *
         * 90 represents [First Quarter][LunarEvent.PhaseEvent.FirstQuarter.Companion]
         *
         * 180 represents [Full Moon][LunarEvent.PhaseEvent.FullMoon.Companion]
         *
         * 270 represents [Last Quarter][LunarEvent.PhaseEvent.LastQuarter.Companion]
         */
        public val phase: Double
    }

    /**
     * An intermediate moon phase. There are only 4 intermediate moon phases. Waxing Crescent, Waxing Gibbous,
     * Waning Gibbous, Waning Crescent. These phases describe the state of the moon between the 4 [Primary] moon phases.
     */
    public sealed interface Intermediate : LunarPhase {
        public companion object {
            // TODO: I get a NPE if this get() is removed. Started on Kotlin 2.2.0.
            internal val all: List<Intermediate> get() =
                listOf(WaxingCrescent, WaxingGibbous, WaningGibbous, WaningCrescent)
        }

        /**
         * Range of moon phase angles where the moon can be considered in this state
         */
        public val angleRange: OpenEndRange<Double>

        /**
         * The midpoint between the upper and lower bound of [angleRange]
         */
        public val midpointAngle: Double get() = (angleRange.start + angleRange.endExclusive) / 2.0

        /**
         * [Intermediate] phase that occurs when the moon is between [New Moon][LunarEvent.PhaseEvent.NewMoon] and the
         * [First Quarter][LunarEvent.PhaseEvent.FirstQuarter] [Primary] phases.
         */
        public data object WaxingCrescent : Intermediate {
            override val angleRange: OpenEndRange<Double> =
                LunarEvent.PhaseEvent.NewMoon.phase..<LunarEvent.PhaseEvent.FirstQuarter.phase
        }

        /**
         * [Intermediate] phase that occurs when the moon is between [First Quarter][LunarEvent.PhaseEvent.FirstQuarter]
         * and the [Full Moon][LunarEvent.PhaseEvent.FullMoon] [Primary] phases.
         */
        public data object WaxingGibbous : Intermediate {
            override val angleRange: OpenEndRange<Double> =
                LunarEvent.PhaseEvent.FirstQuarter.phase..<LunarEvent.PhaseEvent.FullMoon.phase
        }

        /**
         * [Intermediate] phase that occurs when the moon is between [Full Moon][LunarEvent.PhaseEvent.FullMoon]
         * and the [Last Quarter][LunarEvent.PhaseEvent.LastQuarter] [Primary] phases.
         */
        public data object WaningGibbous : Intermediate {
            override val angleRange: OpenEndRange<Double> =
                LunarEvent.PhaseEvent.FullMoon.phase..<LunarEvent.PhaseEvent.LastQuarter.phase
        }

        /**
         * [Intermediate] phase that occurs when the moon is between [Last Quarter][LunarEvent.PhaseEvent.LastQuarter]
         * and the [New Moon][LunarEvent.PhaseEvent.NewMoon] [Primary] phases.
         */
        public data object WaningCrescent : Intermediate {
            override val angleRange: OpenEndRange<Double> =
                LunarEvent.PhaseEvent.LastQuarter.phase..<360.0
        }
    }

    public companion object {

        private val all: List<LunarPhase> = listOf(
            LunarEvent.PhaseEvent.NewMoon,
            Intermediate.WaxingCrescent,
            LunarEvent.PhaseEvent.FirstQuarter,
            Intermediate.WaxingGibbous,
            LunarEvent.PhaseEvent.FullMoon,
            Intermediate.WaningGibbous,
            LunarEvent.PhaseEvent.LastQuarter,
            Intermediate.WaningCrescent
        )

        /**
         * Takes an angle and finds the closest moon phase to that angle. This can return phases like New Moon which
         * only last for an instant in time. If you are attempting to get the "state" of the moon the [lunarPhase]
         * method is technically correct.
         *
         * @param angle Angle of the moon phase. Will be normalized into the range of 0..<360.
         * @see [LunarPhase] For sample angles

         * @return Closest [LunarPhase] that is matching that angle.
         * Includes [LunarEvent.PhaseEvent.NewMoon], [LunarEvent.PhaseEvent.FirstQuarter],
         * [LunarEvent.PhaseEvent.FullMoon], and [LunarEvent.PhaseEvent.LastQuarter].
         */
        public fun closestMoonPhase(angle: Double): LunarPhase {
            // bring into range 0.0..<360.0
            val normalized = angle.normalizedAngle
            return if (normalized > Intermediate.WaningCrescent.midpointAngle +
                ((360.0 - Intermediate.WaningCrescent.midpointAngle) / 2)
            ) {
                LunarEvent.PhaseEvent.NewMoon
            } else {
                return all.minBy {
                    val midpointAngle = when (it) {
                        is Primary -> it.phase
                        is Intermediate -> it.midpointAngle
                    }
                    (midpointAngle - normalized).absoluteValue
                }
            }
        }

        /**
         * Takes an angle and finds the current phase of the moon based on that angle. This will only return
         * intermediate phases like Waning Crescent and not Primary phases like Full Moon as those technically only
         * occur for an instant in time. If you were trying to return the Moon's current phase as a state this is
         * the "technically" correct way to do that. Sometimes we refer to the full Moon as a range of time but there's
         * no agreed upon standard for that so if you wanted that behavior you would need to write your own function.
         *
         * @param angle Angle of the moon phase. Will be normalized into the range of 0..<360.
         * @see [LunarPhase.Intermediate] For angle ranges
         *
         * @return The current [LunarPhase.Intermediate]
         */
        public fun lunarPhase(angle: Double): Intermediate {
            return Intermediate.all.first { it.angleRange.contains(angle.normalizedAngle) }
        }

        private val Double.normalizedAngle get() = (this % 360.0).let { if (it < 0.0) it + 360.0 else it }
    }
}

internal fun LunarPhase.Primary.phaseInformation(
    time: Instant,
): LunarEvent.PhaseEvent = when (this) {
    LunarEvent.PhaseEvent.NewMoon -> LunarEvent.PhaseEvent.NewMoon(time)
    LunarEvent.PhaseEvent.FirstQuarter -> LunarEvent.PhaseEvent.FirstQuarter(time)
    LunarEvent.PhaseEvent.FullMoon -> LunarEvent.PhaseEvent.FullMoon(time)
    LunarEvent.PhaseEvent.LastQuarter -> LunarEvent.PhaseEvent.LastQuarter(time)
}
