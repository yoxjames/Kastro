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

/**
 * Calculates the illumination of the moon.
 */
@Poko
public class LunarIllumination(

    /**
     * Illuminated fraction. `0.0` indicates new moon, `1.0` indicates full
     * moon.
     */
    public val fraction: Double,

    /**
     * Moon phase. Starts at `0.0` (new moon, waxing), passes `180.0` (full moon) and moves toward `360.0`
     * (waning, new moon).
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
)

/**
 * The closest [LunarPhase] to the [LunarIllumination]'s angle.
 *
 * @return Closest [LunarPhase]
 */
public val LunarIllumination.closestPhase: LunarPhase get() = LunarPhase.closestMoonPhase(phase)
