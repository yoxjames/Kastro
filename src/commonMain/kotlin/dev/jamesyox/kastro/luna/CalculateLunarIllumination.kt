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
import dev.jamesyox.kastro.util.Sol
import dev.jamesyox.kastro.util.degrees
import dev.jamesyox.kastro.util.julianDate
import kotlinx.datetime.Instant
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

/**
 * Calculates the [LunarIllumination] for a given instant in time.
 *
 * @receiver The time you wish to calculate [LunarIllumination] for
 * @return The [LunarIllumination]
 * @see LunarIllumination
 */
public fun Instant.calculateLunarIllumination(): LunarIllumination {
    return calculateLunarIllumination(julianDate)
}

private fun calculateLunarIllumination(jd: JulianDate): LunarIllumination {
    val s = Sol.position(jd)
    val m = Moon.position(jd)
    val phi = PI - acos(m.dot(s) / (m.r * s.r))
    val sunMoon = m.cross(s)
    val angle = atan2(
        cos(s.theta) * sin(s.phi - m.phi),
        sin(s.theta) * cos(m.theta) - cos(s.theta) * sin(m.theta) * cos(s.phi - m.phi)
    )
    return LunarIllumination(
        fraction = (1 + cos(phi)) / 2,
        phase = (phi * sign(sunMoon.theta)).degrees + 180.0,
        illuminationAngle = angle.degrees
    )
}
