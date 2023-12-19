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
 * Represents the various Twilight phases the sun can have.
 */
public sealed interface TwilightPhase {
    /**
     * No twilight phase observed. Sun is up.
     */
    public data object Day : DawnPhase, TwilightPhase {
        override val dawnAngle: Double = 0.0
    }

    /**
     * Civil twilight is observed when the sun is between 0.0 and -6.0 degrees.
     */
    public data object CivilTwilight : DawnPhase, DuskPhase, TwilightPhase {
        override val duskAngle: Double = 0.0
        override val dawnAngle: Double = -6.0
    }

    /**
     * Nautical twilight is observed when the sun is between -6.0 and -12.0 degrees.
     */
    public data object NauticalTwilight : DawnPhase, DuskPhase, TwilightPhase {
        override val duskAngle: Double = -6.0
        override val dawnAngle: Double = -12.0
    }

    /**
     * Astronomical twilight is observed when the sun is between -12.0 and -18.0 degrees.
     */
    public data object AstronomicalTwilight : DawnPhase, DuskPhase, TwilightPhase {
        override val duskAngle: Double = -12.0
        override val dawnAngle: Double = -18.0
    }

    /**
     * No twilight phase observed. Sun is down.
     */
    public data object Night : DuskPhase, TwilightPhase {
        override val duskAngle: Double = -18.0
    }
}
