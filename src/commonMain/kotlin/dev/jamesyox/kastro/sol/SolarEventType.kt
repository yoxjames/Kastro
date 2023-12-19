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
 * Types of [SolarEvent]s that can be requested in a [SolarEventSequence]
 */
public sealed interface SolarEventType {
    public companion object {
        /**
         * List of all supported [SolarEventType]s
         */
        public val all: List<SolarEventType> = listOf(
            SolarEvent.Sunset,
            SolarEvent.CivilDusk,
            SolarEvent.NauticalDusk,
            SolarEvent.AstronomicalDusk,
            SolarEvent.Night,
            SolarEvent.AstronomicalDawn,
            SolarEvent.NauticalDawn,
            SolarEvent.CivilDawn,
            SolarEvent.Sunrise,
            SolarEvent.Day,
            SolarEvent.GoldenHourDusk,
            SolarEvent.GoldenHourDawn,
            SolarEvent.BlueHourDusk,
            SolarEvent.BlueHourDawn,
            SolarEvent.Noon,
            SolarEvent.Nadir,
            SolarEvent.SunsetBegin,
            SolarEvent.SunriseEnd,
            SolarEvent.BlueHourDawnEnd,
            SolarEvent.BlueHourDuskEnd
        )

        /**
         * List of simple [SolarEventType]s that should work for most use cases.
         */
        public val simple: List<SolarEventType> = listOf(
            SolarEvent.Sunrise,
            SolarEvent.Sunset,
            SolarEvent.Noon,
            SolarEvent.Nadir
        )
    }

    /**
     * a type representing a Solar Angle. A solar angle is essentially a position of the sun in the sky
     * in terms of the angle of altitude. Since the sun rises and falls many solar angles have two potential meanings
     * either their dusk or dawn version. Those types are represented below.
     */
    public sealed interface Angle : SolarEventType {
        /**
         * Returns the sun's angle at the twilight position, in degrees.
         */
        public val angle: Double

        /**
         * A solar angle where the sun is rising.
         */
        public sealed interface Dawn : Angle

        /**
         * A solar angle where the sun is setting.
         */
        public sealed interface Dusk : Angle
    }

    /**
     * A solar angle observed from a particular point on the earth's surface. Furthermore, this type of solar
     * angle can specify the [angularPosition]  which describes which part of the sun this angle is representing.
     */
    public sealed interface TopocentricAngle : Angle {
        /**
         * Returns the angular position. `0.0` represents the center of the sun. `1.0`
         * represents the upper edge of the sun. `-1.0` represents the lower edge of the sun.
         */
        public val angularPosition: Double
    }

    /**
     * Minimum or Maximum solar angle events. Includes [SolarEvent.Noon] and [SolarEvent.Nadir]
     */
    public sealed interface Culmination : SolarEventType
}

internal fun SolarEventType.Angle.eventAt(time: Instant): SolarEvent = when (this) {
    SolarEvent.CivilDusk -> SolarEvent.CivilDusk(time)
    SolarEvent.NauticalDusk -> SolarEvent.NauticalDusk(time)
    SolarEvent.AstronomicalDusk -> SolarEvent.AstronomicalDusk(time)
    SolarEvent.Night -> SolarEvent.Night(time)
    SolarEvent.AstronomicalDawn -> SolarEvent.AstronomicalDawn(time)
    SolarEvent.NauticalDawn -> SolarEvent.NauticalDawn(time)
    SolarEvent.CivilDawn -> SolarEvent.CivilDawn(time)
    SolarEvent.Day -> SolarEvent.Day(time)
    SolarEvent.GoldenHourDusk -> SolarEvent.GoldenHourDusk(time)
    SolarEvent.GoldenHourDawn -> SolarEvent.GoldenHourDawn(time)
    SolarEvent.BlueHourDusk -> SolarEvent.BlueHourDusk(time)
    SolarEvent.BlueHourDawn -> SolarEvent.BlueHourDawn(time)
    SolarEvent.Sunset -> SolarEvent.Sunset(time)
    SolarEvent.Sunrise -> SolarEvent.Sunrise(time)
    SolarEvent.SunsetBegin -> SolarEvent.SunsetBegin(time)
    SolarEvent.SunriseEnd -> SolarEvent.SunriseEnd(time)
    SolarEvent.BlueHourDawnEnd -> SolarEvent.BlueHourDawnEnd(time)
    SolarEvent.BlueHourDuskEnd -> SolarEvent.BlueHourDuskEnd(time)
}
