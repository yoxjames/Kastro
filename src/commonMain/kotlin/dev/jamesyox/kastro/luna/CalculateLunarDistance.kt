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

import dev.jamesyox.kastro.util.JulianDate
import dev.jamesyox.kastro.util.Moon
import dev.jamesyox.kastro.util.julianDate
import kotlinx.datetime.Instant

/**
 * Calculates the distance of the moon for an instant in time.
 *
 * @receiver Time you wish to calculate moon distance for
 * @return The moon distance in km
 */
public fun Instant.calculateLunarDistance(): Double {
    return julianDate.calculateLunarDistance()
}

internal fun JulianDate.calculateLunarDistance(): Double {
    return Moon.position(this).r
}
