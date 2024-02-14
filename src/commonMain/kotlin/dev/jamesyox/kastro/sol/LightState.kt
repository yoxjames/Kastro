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
 * A state the sun can be in where the angle can cause lighting effects. This is mostly relevant to photographers.
 *
 * All the [LightStates][LightState] are [LightPhases][LightPhase] but these states have different dawn and dusk
 * objects which allow you to distinguish between the two.
 */
public sealed interface LightState {
    /**
     * Represents [LightPhase.GoldenHour] during dawn.
     */
    public data object GoldenHourDawn : LightState, LightPhase.GoldenHour

    /**
     * Represents [LightPhase.GoldenHour] during dusk.
     */
    public data object GoldenHourDusk : LightState, LightPhase.GoldenHour

    /**
     * Represents [LightPhase.BlueHour] during dawn.
     */
    public data object BlueHourDawn : LightState, LightPhase.BlueHour

    /**
     * Represents [LightPhase.BlueHour] during dusk.
     */
    public data object BlueHourDusk : LightState, LightPhase.BlueHour
}

/**
 * A state the sun can be in where the angle can cause lighting effects. This is mostly relevant to photographers.
 *
 * Many [LightPhases][LightPhase] can occur twice during a full solar cycle. For instance [GoldenHour] can occur
 * during both dusk and dawn.
 */
public sealed interface LightPhase {
    /**
     * A time ideal for taking photographs due to the angle of the sun and the pleasing red light
     * it casts. This is sometimes referred to as "magic hour."
     *
     * This occurs when the sun is between 6.0 and -6.0 degrees. It intersects with [BlueHour] a bit.
     */
    public sealed interface GoldenHour : LightPhase {
        public companion object : LightPhaseInfo {
            override val dawnAngle: Double = -6.0
            override val duskAngle: Double = 6.0
        }
    }

    /**
     * When the sun is at a significant depth below the horizon and light takes on a particularly
     * blue shade.
     *
     * This occurs when the sun is between -4.0 and -8.0 degrees. It intersects with [GoldenHour] a bit.
     */
    public sealed interface BlueHour : LightPhase {
        public companion object : LightPhaseInfo {
            override val dawnAngle: Double = -8.0
            override val duskAngle: Double = -4.0
        }
    }
}

/**
 * Describes what a [LightPhase] is by defining the range of angles for which a particular [LightPhase] is active for.
 */
public sealed interface LightPhaseInfo {
    public companion object {
        /**
         * All the [LightPhaseInfo] implementations available.
         */
        public val all: List<LightPhaseInfo> = listOf(LightPhase.BlueHour, LightPhase.GoldenHour)
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
 * Range of angles for which a given [LightPhaseInfo] is active for.
 */
public val LightPhaseInfo.angleRange: ClosedRange<Double> get() = duskAngle..dawnAngle
