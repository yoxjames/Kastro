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

package dev.jamesyox.kastro.util

import kotlin.jvm.JvmInline

@JvmInline
internal value class Longitude(
    internal val value: Double
) {
    init {
        require((-180.0..180.0).contains(value))
    }
}

internal val Double.longitude get() = Longitude(this)

@JvmInline
internal value class Latitude(
    internal val value: Double
) {
    init {
        require((-90.0..90.0).contains(value))
    }
}

internal val Double.latitude get() = Latitude(this)

internal val Latitude.radians get() = value.radians
internal val Longitude.radians get() = value.radians
