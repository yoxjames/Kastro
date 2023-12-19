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
 * A phase the sun can enter during dusk
 */
public interface DuskPhase {
    /**
     * The angle at which this phase begins
     */
    public val duskAngle: Double
}

/**
 * A phase the sun can enter during dawn.
 */
public interface DawnPhase {
    /**
     * The angle at which this phase begins
     */
    public val dawnAngle: Double
}
