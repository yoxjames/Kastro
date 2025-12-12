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

import dev.drewhamilton.poko.Poko
import kotlin.time.Instant

/**
 * Times when particular solar events of interest happen. Examples would be things like
 * Sunrise, sunset, solar noon, start of civil dawn, etc. These events represent moments or [Instants][Instant] in time.
 * This time can be extracted via [SolarEvent.time]
 */
public sealed interface SolarEvent : Comparable<SolarEvent> {
    /**
     * Time of the event
     */
    public val time: Instant

    override fun compareTo(other: SolarEvent): Int = time.compareTo(other.time)

    /**
     * A type of Solar Event that includes the beginning of various [SolarPhase]s
     */
    public sealed interface TwilightEvent : SolarEvent

    /**
     * Horizon Events are solar events that involve the sun transiting the horizon. For instance
     * [Sunrise] and [Sunset]. Also includes events like [SunsetBegin] and [SunriseEnd]
     */
    public sealed interface HorizonEvent : SolarEvent {
        /**
         * Simple HorizonEvents include just [Sunset] and [Sunrise]
         */
        public sealed interface Simple : HorizonEvent
    }

    /**
     * Light events are solar events pertaining to interesting light effects created by the position of the sun.
     * Examples are things like the start of Golden Hour or Blue Hour. These event types are of particular
     * interest to photographers.
     */
    public sealed interface LightEvent : SolarEvent

    /**
     * Moment when the top edge of the sun completely disappears behind the horizon
     */
    @Poko
    public class Sunset(
        override val time: Instant
    ) : HorizonEvent.Simple {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dusk, SolarEventType {
            override val angle: Double = 0.0
            override val angularPosition: Double = 1.0
        }
    }

    /**
     * Moment when the sun sets to 0.0 degrees. Begins [SolarPhase.CivilDusk].
     */
    @Poko
    public class CivilDusk(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = SolarPhase.CivilDusk.startAngle
        }
    }

    /**
     * Moment when the sun sets to -6.0 degrees. Begins [SolarPhase.NauticalDusk].
     */
    @Poko
    public class NauticalDusk(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = SolarPhase.NauticalDusk.startAngle
        }
    }

    /**
     * Moment when the sun sets to -12.0 degrees. Begins [SolarPhase.AstronomicalDusk].
     */
    @Poko
    public class AstronomicalDusk(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = SolarPhase.AstronomicalDusk.startAngle
        }
    }

    /**
     * Moment when the sun sets to -18.0 degrees. Begins [SolarPhase.Night].
     */
    @Poko
    public class Night(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = SolarPhase.Night.startAngle
        }
    }

    /**
     * Moment when the sun rises to -18.0 degrees. Begins [SolarPhase.AstronomicalDawn].
     */
    @Poko
    public class AstronomicalDawn(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = SolarPhase.AstronomicalDawn.startAngle
        }
    }

    /**
     * Moment when the sun rises to -12.0 degrees. Begins [SolarPhase.NauticalDawn].
     */
    @Poko
    public class NauticalDawn(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = SolarPhase.NauticalDawn.startAngle
        }
    }

    /**
     * Moment when the sun rises to -6.0 degrees. Begins [SolarPhase.CivilDawn].
     */
    @Poko
    public class CivilDawn(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = SolarPhase.CivilDawn.startAngle
        }
    }

    /**
     * Moment when the top edge of the sun first rises from the horizon.
     */
    @Poko
    public class Sunrise(
        override val time: Instant
    ) : HorizonEvent.Simple {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dawn {
            override val angle: Double = 0.0
            override val angularPosition: Double = 1.0
        }
    }

    /**
     * Moment when the solar angle rises to 0.0. Begins [SolarPhase.Day].
     */
    @Poko
    public class Day(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = SolarPhase.Day.startAngle
        }
    }

    /**
     * When the solar angle sets to 0.0. Marks the start of [LightState.GoldenHourDusk].
     */
    @Poko
    public class GoldenHourDusk(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightPhase.GoldenHour.duskAngle
        }
    }

    /**
     * When the solar angle rises to -6.0. Marks the start of [LightState.GoldenHourDawn].
     */
    @Poko
    public class GoldenHourDawn(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightPhase.GoldenHour.dawnAngle
        }
    }

    /**
     * When the solar angle sets to -4.0. Marks the start of [LightState.BlueHourDusk].
     */
    @Poko
    public class BlueHourDusk(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightPhase.BlueHour.duskAngle
        }
    }

    /**
     * When the solar angle rises to -8.0. Marks the start of [LightState.BlueHourDawn].
     */
    @Poko
    public class BlueHourDawn(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightPhase.BlueHour.dawnAngle
        }
    }

    /**
     * Solar noon. When the sun is at its highest position in the sky for a given cycle.
     */
    @Poko
    public class Noon(
        override val time: Instant
    ) : SolarEvent {
        public companion object : SolarEventType.Culmination
    }

    /**
     * The opposite of solar noon. When the sun is at its lowest position in the sky for a given cycle.
     */
    @Poko
    public class Nadir(
        override val time: Instant
    ) : SolarEvent {
        public companion object : SolarEventType.Culmination
    }

    /**
     * When the bottom edge of the sun touches the horizon when the sun is setting.
     */
    @Poko
    public class SunsetBegin(
        override val time: Instant
    ) : HorizonEvent {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dusk {
            override val angle: Double = 0.0
            override val angularPosition: Double = -1.0
        }
    }

    /**
     * When the bottom edge of the sun rises above the horizon.
     */
    @Poko
    public class SunriseEnd(
        override val time: Instant
    ) : HorizonEvent {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dawn {
            override val angle: Double = 0.0
            override val angularPosition: Double = -1.0
        }
    }

    /**
     * The end of [LightState.BlueHourDusk]. Happens when the sun sets past -8.0 degrees.
     */
    @Poko
    public class BlueHourDuskEnd(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightPhase.BlueHour.dawnAngle
        }
    }

    /**
     * The end of [LightState.BlueHourDawn]. Happens when the sun rises past -4.0 degrees.
     */
    @Poko
    public class BlueHourDawnEnd(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightPhase.BlueHour.duskAngle
        }
    }

    /**
     * The end of [LightState.GoldenHourDusk]. Happens when the sun sets past - 6.0 degrees.
     */
    @Poko
    public class GoldenHourDuskEnd(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightPhase.GoldenHour.dawnAngle
        }
    }

    /**
     * The end of [LightState.GoldenHourDawn]. Happens when the sun rises past 6.0 degrees.
     */
    @Poko
    public class GoldenHourDawnEnd(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightPhase.GoldenHour.duskAngle
        }
    }
}
