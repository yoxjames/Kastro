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
 * States of the sun relevant to lighting effects. These states are of particular interest
 * to photographers.
 */
public enum class LightState : DawnPhase, DuskPhase {
    /**
     * A time ideal for taking photographs due to the angle of the sun and the pleasing red light
     * it casts. This is sometimes referred to as "magic hour."
     *
     * This occurs when the sun is between 6.0 and -6.0 degrees. It intersects with [BlueHour] a bit.
     */
    GoldenHour {
        override val dawnAngle: Double = -6.0
        override val duskAngle: Double = 6.0
    },

    /**
     * When the sun is at a significant depth below the horizon and light takes on a particularly
     * blue shade.
     *
     * This occurs when the sun is between -4.0 and -8.0 degrees. It intersects with [GoldenHour] a bit.
     */
    BlueHour {
        override val dawnAngle: Double = -8.0
        override val duskAngle: Double = -4.0
    }
}
