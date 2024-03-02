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

/**
 * Represents the "state" of the sun. [SolarEvents][SolarEvent] represent moments in time where state transitions may
 * occur. [SolarPhase], in contrast, represents the ranges between [SolarEvents][SolarEvent]. For a given location
 * there is always a [SolarPhase] to describe the sun at any given [kotlinx.datetime.Instant]
 */
public sealed interface SolarPhase {
    /**
     * The angle that this [SolarPhase] begins at.
     */
    public val startAngle: Double

    /**
     * No twilight phase observed. Sun is up.
     */
    public data object Day : SolarPhase {
        override val startAngle: Double = 0.0
    }

    /**
     * No twilight phase observed. Sun is down.
     */
    public data object Night : SolarPhase {
        override val startAngle: Double = -18.0
    }

    /**
     * The sun is in the range of [CivilTwilight][Twilight.CivilTwilight] during dawn.
     */
    public data object CivilDawn : SolarPhase, Twilight.CivilTwilight {
        override val startAngle: Double = Twilight.CivilTwilight.dawnAngle
    }

    /**
     * The sun is in the range of [NauticalTwilight][Twilight.NauticalTwilight] during dawn.
     */
    public data object NauticalDawn : SolarPhase, Twilight.NauticalTwilight {
        override val startAngle: Double = Twilight.NauticalTwilight.dawnAngle
    }

    /**
     * The sun is in the range of [AstronomicalTwilight][Twilight.AstronomicalTwilight] during dawn.
     */
    public data object AstronomicalDawn : SolarPhase, Twilight.AstronomicalTwilight {
        override val startAngle: Double = Twilight.AstronomicalTwilight.dawnAngle
    }

    /**
     * The sun is in the range of [AstronomicalTwilight][Twilight.AstronomicalTwilight] during dusk.
     */
    public data object AstronomicalDusk : SolarPhase, Twilight.AstronomicalTwilight {
        override val startAngle: Double = Twilight.AstronomicalTwilight.duskAngle
    }

    /**
     * The sun is in the range of [NauticalTwilight][Twilight.NauticalTwilight] during dusk.
     */
    public data object NauticalDusk : SolarPhase, Twilight.NauticalTwilight {
        override val startAngle: Double = Twilight.NauticalTwilight.duskAngle
    }

    /**
     * The sun is in the range of [CivilTwilight][Twilight.CivilTwilight] during dusk.
     */
    public data object CivilDusk : SolarPhase, Twilight.CivilTwilight {
        override val startAngle: Double = Twilight.CivilTwilight.duskAngle
    }
}

/**
 * [Twilights][Twilight] are states of the sun defined by the number of degrees below the horizon.
 * Each [Twilight] has a [TwilightInfo.angleRange] that defines the range of angles for which the sun
 * would be considered to be in that associated [Twilight].
 *
 * [CivilTwilight][Twilight.CivilTwilight], [NauticalTwilight][Twilight.NauticalTwilight],
 * [AstronomicalTwilight][Twilight.AstronomicalTwilight] would be [Twilights][Twilight] but
 * [SolarPhase.Day] and [SolarPhase.Night] are **not** considered [Twilights][Twilight].
 *
 * Many [Twilights][Twilight] can occur twice during a full solar cycle. For instance,
 * [CivilTwilight][Twilight.CivilTwilight] can occur during both dusk and dawn, [SolarPhase.CivilDusk]
 * and [SolarPhase.CivilDawn] respectively. Those are both considered to be [CivilTwilight][Twilight.CivilTwilight].
 */
public sealed interface Twilight {
    /**
     * Civil twilight is observed when the sun is between 0.0 and -6.0 degrees.
     */
    public sealed interface CivilTwilight : Twilight {
        public companion object : TwilightInfo {
            override val duskAngle: Double = 0.0
            override val dawnAngle: Double = -6.0
        }
    }

    /**
     * Nautical twilight is observed when the sun is between -6.0 and -12.0 degrees.
     */
    public sealed interface NauticalTwilight : Twilight {
        public companion object : TwilightInfo {
            override val duskAngle: Double = -6.0
            override val dawnAngle: Double = -12.0
        }
    }

    /**
     * Astronomical twilight is observed when the sun is between -12.0 and -18.0 degrees.
     */
    public sealed interface AstronomicalTwilight : Twilight {
        public companion object : TwilightInfo {
            override val duskAngle: Double = -12.0
            override val dawnAngle: Double = -18.0
        }
    }
}

/**
 * Describes what a [Twilight] is by defining the range of angles for which a particular [Twilight] is active for.
 */
public sealed interface TwilightInfo {
    public companion object {
        /**
         * All the [TwilightInfo] implementations available.
         */
        public val all: List<TwilightInfo> = listOf(
            Twilight.CivilTwilight,
            Twilight.NauticalTwilight,
            Twilight.AstronomicalTwilight
        )
    }

    /**
     * The angle at which this phase begins
     */
    public val duskAngle: Double

    /**
     * The angle at which this phase begins
     */
    public val dawnAngle: Double
}

/**
 * Range of angles for which a given [TwilightInfo] is active for.
 */
public val TwilightInfo.angleRange: ClosedRange<Double> get() = dawnAngle..duskAngle
