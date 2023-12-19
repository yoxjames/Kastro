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

package dev.jamesyox.kastro.common

/**
 * Whether a celestial object is rising or setting at a given instant in time.
 */
public enum class HorizonMovementState {

    /**
     * The celestial object in question is rising (getting higher in the sky)
     */
    Rising,

    /**
     * The celestial object in question is setting (getting lower in the sky)
     */
    Setting
}

internal fun fromAzimuth(azimuth: Double): HorizonMovementState = when (azimuth) {
    in (0.0..180.0) -> HorizonMovementState.Rising
    else -> HorizonMovementState.Setting
}
