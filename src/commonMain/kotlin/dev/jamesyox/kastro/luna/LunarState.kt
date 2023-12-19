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

import dev.jamesyox.kastro.common.HorizonMovementState
import dev.jamesyox.kastro.common.HorizonState
import dev.jamesyox.kastro.common.fromAzimuth

/**
 * Single class containing all the information kAstro can calculate about Luna for a given instant of time. Contains
 * the result of multiple calculations.
 */
public class LunarState internal constructor(
    /**
     * Information pertaining to the Lunar position
     */
    public val position: LunarPosition,

    /**
     * Information pertaining to Lunar illumination
     */
    public val illumination: LunarIllumination,
) {
    /**
     * The phase of the moon. This can only be an [LunarPhase.Intermediate] phase as [LunarPhase.Primary] only occur
     * for an instant.
     */
    public val phase: LunarPhase.Intermediate = LunarPhase.lunarPhase(illumination.angle)

    /**
     * Whether Luna (the moon) is above or below the horizon
     */
    public val horizonState: HorizonState = when (position.altitude > 0.0) {
        true -> HorizonState.Up
        false -> HorizonState.Down
    }

    /**
     * Whether the moon is rising or setting
     */
    public val horizonMovementState: HorizonMovementState = fromAzimuth(position.azimuth)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LunarState

        if (position != other.position) return false
        if (illumination != other.illumination) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + illumination.hashCode()
        return result
    }

    override fun toString(): String {
        return "LunarState(position=$position, illumination=$illumination, phase=$phase, horizonState=$horizonState)"
    }
}
