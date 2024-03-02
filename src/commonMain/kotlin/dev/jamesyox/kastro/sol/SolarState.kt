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

import dev.jamesyox.kastro.common.HorizonMovementState
import dev.jamesyox.kastro.common.HorizonState
import dev.jamesyox.kastro.common.fromAzimuth
import dev.jamesyox.kastro.util.Moon.angularRadius
import dev.jamesyox.kastro.util.degrees

/**
 * Single class containing all the information Kastro can calculate about Sol for a given instant of time. Contains
 * the result of multiple calculations.
 */
public class SolarState internal constructor(
    /**
     * Sun azimuth, in degrees, north-based.
     *
     *
     * This is the direction along the horizon, measured from north to east. For example,
     * `0.0` means north, `135.0` means southeast, `270.0` means west.
     */
    public val azimuth: Double,

    /**
     * Sun's distance, in kilometers.
     */
    public val distance: Double,

    internal val atmosphericRefractionRad: Double,
    internal val trueAltitudeRad: Double,
    internal val parallaxRad: Double,
) {
    /**
     * The atmospheric refraction of the Sun's light in degrees
     */
    public val atmosphericRefraction: Double = atmosphericRefractionRad.degrees

    /**
     * The true altitude of the center of the sun above the horizon.
     *
     *
     * `0.0` means the sun's center is at the horizon, `90.0` at the zenith
     * (straight over your head).
     *
     * @see altitude
     */
    public val trueAltitude: Double = trueAltitudeRad.degrees

    /**
     * The parallax, in degrees.
     */
    public val parallax: Double = parallaxRad.degrees

    /**
     * The visible altitude of the center of the sun above the horizon.
     *
     *
     * `0.0` means the sun's center is at the horizon, `90.0` at the zenith
     * (straight over your head). Atmospheric refraction is taken into account.
     *
     * @see trueAltitude
     */
    public val altitude: Double = trueAltitude + (atmosphericRefraction - parallax)

    private fun angularPositionOffset(angularPosition: Double): Double {
        return angularPosition * angularRadius(distance)
    }

    /**
     * Obtains the altitude of a particular position of the sun above the horizon.
     *
     * @param angularPosition Position (up and down) on the sun to calculate the altitude at.
     * `1.0` would be the top edge of the sun.
     * `-1.0` would be the bottom edge of the sun.
     * `0.0` would be the center of the sun.
     */
    public fun altitudeAt(angularPosition: Double): Double {
        return altitude + angularPositionOffset(angularPosition)
    }

    /**
     * Whether the sun is [HorizonState.Up] or [HorizonState.Down]
     */
    public val horizonState: HorizonState
        get() = when (altitudeAt(1.0) > 0.0) {
            true -> HorizonState.Up
            false -> HorizonState.Down
        }

    /**
     * Whether the Sun is rising or setting.
     */
    public val horizonMovementState: HorizonMovementState = fromAzimuth(azimuth)

    /**
     * The current active [LightState]s. There can be more than one active [LightState] at a time.
     * For instance, [LightState.GoldenHourDusk] and [LightState.BlueHourDusk] intersect a bit, so it's possible
     * to have both of those or neither. Can be empty.
     */
    public val lightStates: List<LightState> = LightPhaseInfo.all
        .filter { trueAltitude in it.angleRange }
        .map {
            when (it) {
                LightPhase.GoldenHour -> when (horizonMovementState) {
                    HorizonMovementState.Rising -> LightState.GoldenHourDawn
                    HorizonMovementState.Setting -> LightState.GoldenHourDusk
                }
                LightPhase.BlueHour -> when (horizonMovementState) {
                    HorizonMovementState.Rising -> LightState.BlueHourDawn
                    HorizonMovementState.Setting -> LightState.BlueHourDusk
                }
            }
        }

    /**
     * The current [SolarPhase]. There can be only one active [SolarPhase] at a given time
     */
    public val solarPhase: SolarPhase = when {
        trueAltitude in Twilight.CivilTwilight.angleRange -> when (horizonMovementState) {
            HorizonMovementState.Rising -> SolarPhase.CivilDawn
            HorizonMovementState.Setting -> SolarPhase.CivilDusk
        }
        trueAltitude in Twilight.NauticalTwilight.angleRange -> when (horizonMovementState) {
            HorizonMovementState.Rising -> SolarPhase.NauticalDawn
            HorizonMovementState.Setting -> SolarPhase.NauticalDusk
        }
        trueAltitude in Twilight.AstronomicalTwilight.angleRange -> when (horizonMovementState) {
            HorizonMovementState.Rising -> SolarPhase.AstronomicalDawn
            HorizonMovementState.Setting -> SolarPhase.AstronomicalDusk
        }
        trueAltitude > 0.0 -> SolarPhase.Day
        else -> SolarPhase.Night
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SolarState

        if (azimuth != other.azimuth) return false
        if (distance != other.distance) return false
        if (atmosphericRefractionRad != other.atmosphericRefractionRad) return false
        if (trueAltitudeRad != other.trueAltitudeRad) return false
        if (parallaxRad != other.parallaxRad) return false

        return true
    }

    override fun hashCode(): Int {
        var result = azimuth.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + atmosphericRefractionRad.hashCode()
        result = 31 * result + trueAltitudeRad.hashCode()
        result = 31 * result + parallaxRad.hashCode()
        return result
    }

    override fun toString(): String {
        return "SunState(" +
            "azimuth=$azimuth, " +
            "distance=$distance, " +
            "atmosphericRefraction=$atmosphericRefraction, " +
            "trueAltitude=$trueAltitude, " +
            "parallax=$parallax, " +
            "altitude=$altitude, " +
            "horizonState=$horizonState, " +
            "solarPhase=$solarPhase, " +
            "lightStates=$lightStates, " +
            "horizonMovementState=$horizonMovementState" +
            ")"
    }
}
