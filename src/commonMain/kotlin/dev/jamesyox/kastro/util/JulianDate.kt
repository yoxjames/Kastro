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

import dev.jamesyox.kastro.util.ExtendedMath.frac
import dev.jamesyox.kastro.util.JulianDate.Companion.j1970
import dev.jamesyox.kastro.util.JulianDate.Companion.j2000
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.time.Instant

internal const val daysInJulianCentury = 36525.0
private const val secondsInDay = 86400.0

internal data class JulianDate(
    internal val value: Double
) {
    internal companion object {
        internal val j2000 = JulianDate(2451545.0)
        internal val j1970 = JulianDate(2440587.5)
    }

    /**
     * Returns the Julian Centuries.
     *
     * @return Julian Centuries, based on J2000 epoch, UTC.
     */
    internal val julianCentury: Double get() = (value - j2000.value) / daysInJulianCentury

    fun atHour(hour: Double): JulianDate {
        return JulianDate(value + hour / 24.0)
    }

    internal val mjd = value - 2400000.5
    internal val tmjd = floor(mjd)
}

internal val Instant.julianDate get() = JulianDate((toEpochMilliseconds() / 86400000.0) + j1970.value)
internal val JulianDate.instant get() = Instant.fromEpochMilliseconds(
    ((value - j1970.value) * 86400000.0).roundToLong()
)

/**
 * Returns a [JulianDate] of the given Julian century.
 *
 * @param jc
 * Julian Century
 * @return [JulianDate] instance.
 */
internal fun atJulianCentury(jc: Double): JulianDate {
    return JulianDate(jc * daysInJulianCentury + j2000.value)
}

/**
 * Returns the Greenwich Mean Sidereal Time of this Julian Date.
 *
 * @return GMST
 */
internal val JulianDate.greenwichMeanSiderealTime: Double get() {
    val ut = (mjd - tmjd) * secondsInDay
    val t0 = (tmjd - j2000.mjd) / daysInJulianCentury
    val t = (mjd - j2000.mjd) / daysInJulianCentury
    val gmst = 24110.54841 + 8640184.812866 * t0 + 1.0027379093 * ut + (0.093104 - 6.2e-6 * t) * t * t
    return ExtendedMath.PI2 / secondsInDay * (gmst % secondsInDay)
}

/**
 * Returns the earth's true anomaly of the current date.
 *
 *
 * A simple approximation is used here.
 *
 * @return True anomaly, in radians
 */
internal val JulianDate.trueAnomaly: Double get() {
    val dayOfYear = instant.toLocalDateTime(TimeZone.UTC).dayOfYear
    return (PI * 2.0) * frac((dayOfYear - 5.0) / 365.256363)
}
