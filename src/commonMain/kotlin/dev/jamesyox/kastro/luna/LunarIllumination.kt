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

/**
 * Calculates the illumination of the moon.
 */
public class LunarIllumination(

    /**
     * Illuminated fraction. `0.0` indicates new moon, `1.0` indicates full
     * moon.
     */
    public val fraction: Double,

    /**
     * Moon phase. Starts at `-180.0` (new moon, waxing), passes `0.0` (full
     * moon) and moves toward `180.0` (waning, new moon).
     *
     */
    public val phase: Double,

    /**
     * The angle of the moon illumination relative to earth. The moon is waxing if the
     * angle is negative, and waning if positive.
     *
     *
     * By subtracting [LunarPosition.parallacticAngle] from [illuminationAngle],
     * one can get the zenith angle of the moons bright limb (anticlockwise). The zenith
     * angle can be used do draw the moon shape from the observer's perspective (e.g. the
     * moon lying on its back).
     */
    public val illuminationAngle: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LunarIllumination

        if (fraction != other.fraction) return false
        if (phase != other.phase) return false
        if (illuminationAngle != other.illuminationAngle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fraction.hashCode()
        result = 31 * result + phase.hashCode()
        result = 31 * result + illuminationAngle.hashCode()
        return result
    }

    override fun toString(): String {
        return "LunarIllumination(fraction=$fraction, phase=$phase, angle=$illuminationAngle)"
    }
}

/**
 * The closest [LunarPhase] to the [LunarIllumination]'s angle.
 *
 * @return Closest [LunarPhase]
 */
public val LunarIllumination.closestPhase: LunarPhase get() = LunarPhase.closestMoonPhase(phase + 180.0)
