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

import dev.drewhamilton.poko.Poko
import kotlinx.datetime.Instant

/**
 * Times when particular lunar events of interest happen. Examples would be moon set and moon rise.
 */
public sealed interface LunarEvent : Comparable<LunarEvent> {
    public companion object {
        /**
         * List of all types of [LunarEvent]s that can be requested
         */
        public val all: List<LunarEventType> = HorizonEvent.all + LunarPhase.Primary.all
    }

    /**
     * Types of [LunarEvent]s that can be requested in a lunar event sequence
     */
    public sealed interface LunarEventType

    /**
     * A subtype of [LunarEvent] that includes all events pertaining to the horizon (moonrise and moonset)
     */
    public sealed interface HorizonEvent : LunarEvent {
        /**
         * Types of [LunarEvent.HorizonEvent]s that can be requested in a lunar event sequence
         */
        public sealed interface HorizonEventType : LunarEventType

        public companion object {
            /**
             * List of all valid [HorizonEventType] includes moonset and moonrise.
             */
            public val all: List<HorizonEventType> = listOf(
                Moonrise,
                Moonset
            )
        }

        /**
         * When the moon rises above the horizon.
         */
        @Poko
        public class Moonrise(
            override val time: Instant
        ) : HorizonEvent {
            /**
             * [HorizonEventType] for [Moonrise].
             */
            public companion object : HorizonEventType
        }

        /**
         * When the moon sets below the horizon.
         */
        @Poko
        public class Moonset(
            override val time: Instant
        ) : HorizonEvent {
            /**
             * [HorizonEventType] for [Moonset].
             */
            public companion object : HorizonEventType
        }
    }

    /**
     * Moment of the lunar event
     */
    public val time: Instant

    override fun compareTo(other: LunarEvent): Int = time.compareTo(other.time)

    /**
     * Event pertaining to the phase of the moon. These events represent [LunarPhase.Primary] phases as those occur
     * at a specific instant in time. Waxing phases are where the moon is becoming more illuminated (trending towards
     * a Full Moon, whereas Waning phases are where the moon is becoming less illuminated.
     *
     * Note: Due to the simplified formulas used in Kastro, the returned time can have an
     * error of several minutes.
     */
    public sealed interface PhaseEvent : LunarEvent {

        /**
         * Companion object to essentially create an alias to [LunarPhase.Primary]
         */
        public companion object {
            /**
             * Alias to [LunarPhase.Primary.all].
             *
             * @see [LunarPhase.Primary.all]
             */
            public val all: List<LunarPhase.Primary> = LunarPhase.Primary.all
        }

        public override val time: Instant

        /**
         * Moment when the Sun and Moon are aligned and the lunar disc is not illuminated. This event also is the start
         * of the intermediate phase [LunarPhase.Intermediate.WaxingCrescent].
         *
         * See [Wikipedia: New Moon](https://en.wikipedia.org/wiki/New_moon)
         */
        @Poko
        public class NewMoon(
            override val time: Instant,
        ) : PhaseEvent {
            public companion object : LunarPhase.Primary {
                override val phase: Double = 0.0
            }
        }

        /**
         * Moment when half of the lunar disk is illuminated. This event also is the start of the intermediate phase
         * [LunarPhase.Intermediate.WaxingGibbous].
         */
        @Poko
        public class FirstQuarter(
            override val time: Instant,
        ) : PhaseEvent {
            public companion object : LunarPhase.Primary {
                override val phase: Double = 90.0
            }
        }

        /**
         * Moment when all of the lunar disk is illuminated. This event also is the start of the intermediate phase
         * [LunarPhase.Intermediate.WaningGibbous].
         */
        @Poko
        public class FullMoon(
            override val time: Instant,
        ) : PhaseEvent {
            public companion object : LunarPhase.Primary {
                override val phase: Double = 180.0
            }
        }

        /**
         * Moment when half of the lunar disk is illuminated. This event also is the start of the intermediate phase
         * [LunarPhase.Intermediate.WaningCrescent].
         */
        @Poko
        public class LastQuarter(
            override val time: Instant,
        ) : PhaseEvent {
            public companion object : LunarPhase.Primary {
                override val phase: Double = 270.0
            }
        }
    }
}
