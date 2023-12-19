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

import kotlinx.datetime.Instant

/**
 * Times when particular solar events of interest happen. Examples would be things like
 * Sunrise, sunset, solar noon, start of civil dawn, etc.
 */
public sealed interface SolarEvent : Comparable<SolarEvent> {
    /**
     * Time of the event
     */
    public val time: Instant

    override fun compareTo(other: SolarEvent): Int = time.compareTo(other.time)

    /**
     * A type of Solar Event that includes the beginning of various [TwilightPhase]s
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
     * Sunset is defined as when the top edge of the sun completely disappears behind the horizon
     */
    public class Sunset(
        override val time: Instant
    ) : HorizonEvent.Simple {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dusk, SolarEventType {
            override val angle: Double = 0.0
            override val angularPosition: Double = 1.0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Sunset

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "Sunset(time=$time)"
        }
    }

    /**
     * Begins when the sun sets to 0.0 degrees and ends when the sun sets to -6.0 degrees.
     * @see [TwilightPhase.CivilTwilight]
     */
    public class CivilDusk(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = TwilightPhase.CivilTwilight.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as CivilDusk

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "CivilDusk(time=$time)"
        }
    }

    /**
     * Begins when the sun sets to -6.0 degrees and ends when the sun sets to -12.0 degrees.
     * @see [TwilightPhase.NauticalTwilight]
     */
    public class NauticalDusk(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = TwilightPhase.NauticalTwilight.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as NauticalDusk

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "NauticalDusk(time=$time)"
        }
    }

    /**
     * Begins when the sun sets to -12.0 degrees and ends when the sun sets to -18.0 degrees.
     * See [TwilightPhase.AstronomicalTwilight]
     */
    public class AstronomicalDusk(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = TwilightPhase.AstronomicalTwilight.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as AstronomicalDusk

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "AstronomicalDusk(time=$time)"
        }
    }

    /**
     * Begins when AstronomicalDawn ends when the sun sets to -18.0 degrees. Continues until
     * the start of [AstronomicalDawn] when the sun rises to -18.0 degrees.
     */
    public class Night(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = TwilightPhase.Night.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Night

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "Night(time=$time)"
        }
    }

    /**
     * Begins when the sun rises to -18.0 degrees. Ends when the sun rises to -12.0 degrees.
     * @see [TwilightPhase.AstronomicalTwilight]
     */
    public class AstronomicalDawn(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = TwilightPhase.AstronomicalTwilight.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as AstronomicalDawn

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "AstronomicalDawn(time=$time)"
        }
    }

    /**
     * Begins when the sun rises to -12.0 degrees. Ends when the sun rises to -6.0 degrees.
     * @see [TwilightPhase.NauticalTwilight]
     */
    public class NauticalDawn(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = TwilightPhase.NauticalTwilight.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as NauticalDawn

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "NauticalDawn(time=$time)"
        }
    }

    /**
     * Begins when the sun rises to -6.0 degrees. Ends when the sun rises to 0.0 degrees.
     * @see [TwilightPhase.CivilTwilight]
     */
    public class CivilDawn(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = TwilightPhase.CivilTwilight.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as CivilDawn

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "CivilDawn(time=$time)"
        }
    }

    /**
     * Sunrise is defined as when the top edge of the sun first rises from the horizon.
     */
    public class Sunrise(
        override val time: Instant
    ) : HorizonEvent.Simple {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dawn {
            override val angle: Double = 0.0
            override val angularPosition: Double = 1.0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Sunrise

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "Sunrise(time=$time)"
        }
    }

    /**
     * When the solar angle rises to 0.0. This event marks the end of [CivilDawn]
     */
    public class Day(
        override val time: Instant
    ) : TwilightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = TwilightPhase.Day.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Day

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "Day(time=$time)"
        }
    }

    /**
     * When the solar angle sets to 0.0. Marks the start of [LightState.GoldenHour] during dusk
     */
    public class GoldenHourDusk(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightState.GoldenHour.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as GoldenHourDusk

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "GoldenHourDusk(time=$time)"
        }
    }

    /**
     * When the solar angle rises to -6.0. Marks the start of [LightState.GoldenHour] during dawn.
     */
    public class GoldenHourDawn(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightState.GoldenHour.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as GoldenHourDawn

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "GoldenHourDawn(time=$time)"
        }
    }

    /**
     * When the solar angle sets to -4.0. Marks the start of [LightState.BlueHour] during dusk.
     */
    public class BlueHourDusk(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightState.BlueHour.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as BlueHourDusk

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "BlueHourDusk(time=$time)"
        }
    }

    /**
     * When the solar angle rises to -8.0. Marks the start of [LightState.BlueHour] during dawn.
     */
    public class BlueHourDawn(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightState.BlueHour.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as BlueHourDawn

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "BlueHourDawn(time=$time)"
        }
    }

    /**
     * Solar noon. When the sun is at its highest position in the sky for a given cycle.
     */
    public class Noon(
        override val time: Instant
    ) : SolarEvent {
        public companion object : SolarEventType.Culmination

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Noon

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "Noon(time=$time)"
        }
    }

    /**
     * The opposite of solar noon. When the sun is at its lowest position in the sky for a given cycle.
     */
    public class Nadir(
        override val time: Instant
    ) : SolarEvent {
        public companion object : SolarEventType.Culmination

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Nadir

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "Nadir(time=$time)"
        }
    }

    /**
     * When the bottom edge of the sun touches the horizon when the sun is setting.
     */
    public class SunsetBegin(
        override val time: Instant
    ) : HorizonEvent {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dusk {
            override val angle: Double = 0.0
            override val angularPosition: Double = -1.0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SunsetBegin

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "SunsetBegin(time=$time)"
        }
    }

    /**
     * When the bottom edge of the sun rises above the horizon.
     */
    public class SunriseEnd(
        override val time: Instant
    ) : HorizonEvent {
        public companion object : SolarEventType.TopocentricAngle, SolarEventType.Angle.Dawn {
            override val angle: Double = 0.0
            override val angularPosition: Double = -1.0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SunriseEnd

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "SunriseEnd(time=$time)"
        }
    }

    /**
     * The end of [LightState.BlueHour] during dusk. Happens when the sun sets past -8.0 degrees.
     */
    public class BlueHourDuskEnd(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dusk {
            override val angle: Double = LightState.BlueHour.dawnAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as BlueHourDuskEnd

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "BlueHourDuskEnd(time=$time)"
        }
    }

    /**
     * The end of [LightState.BlueHour] during dawn. Happens when the run rises past -4.0 degrees.
     */
    public class BlueHourDawnEnd(
        override val time: Instant
    ) : LightEvent {
        public companion object : SolarEventType.Angle.Dawn {
            override val angle: Double = LightState.BlueHour.duskAngle
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as BlueHourDawnEnd

            return time == other.time
        }

        override fun hashCode(): Int {
            return time.hashCode()
        }

        override fun toString(): String {
            return "BlueHourDawnEnd(time=$time)"
        }
    }
}
