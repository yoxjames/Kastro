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

import dev.jamesyox.kastro.util.degrees

/**
 * Calculates the position of the moon.
 */
public class LunarPosition internal constructor(
    /**
     * Distance to the moon in kilometers.
     */
    public val distance: Double,
    /**
     * Moon azimuth, in degrees, north-based.
     *
     *
     * This is the direction along the horizon, measured from north to east. For example,
     * `0.0` means north, `135.0` means southeast, `270.0` means west.
     */
    public val azimuth: Double,

    /**
     * Moon altitude above the horizon, in degrees.
     *
     *
     * `0.0` means the moon's center is at the horizon, `90.0` at the zenith
     * (straight over your head).
     */
    public val altitude: Double,

    private val parallacticAngleRad: Double,

) {
    /**
     * Parallactic angle of the moon, in degrees.
     */
    public val parallacticAngle: Double = parallacticAngleRad.degrees

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LunarPosition

        if (distance != other.distance) return false
        if (azimuth != other.azimuth) return false
        if (altitude != other.altitude) return false
        if (parallacticAngle != other.parallacticAngle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = distance.hashCode()
        result = 31 * result + azimuth.hashCode()
        result = 31 * result + altitude.hashCode()
        result = 31 * result + parallacticAngle.hashCode()
        return result
    }

    override fun toString(): String {
        return "LunarPosition(" +
            "distance=$distance, " +
            "azimuth=$azimuth, " +
            "altitude=$altitude, " +
            "parallacticAngle=$parallacticAngle" +
            ")"
    }
}

/**
 * Checks if the moon is in a SuperMoon position.
 *
 *
 * Note that there is no official definition of supermoon. We will assume a
 * supermoon if the center of the moon is closer than 360,000 km to the center of
 * Earth. Usually only full moons or new moons are candidates for supermoons.
 */
public val LunarPosition.isSuperMoon: Boolean get() = distance < 360000.0

/**
 * Checks if the moon is in a MicroMoon position.
 *
 *
 * Note that there is no official definition of micromoon. We will assume a
 * micromoon if the center of the moon is farther than 405,000 km from the center of
 * Earth. Usually only full moons or new moons are candidates for micromoons.
 */
public val LunarPosition.isMicroMoon: Boolean get() = distance > 405000.0
