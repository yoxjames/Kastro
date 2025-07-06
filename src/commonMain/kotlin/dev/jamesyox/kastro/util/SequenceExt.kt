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

/**
 * Method that merges two sorted sequences containing [Comparable] to a sorted resultant sequence.
 * There was nothing in the Kotlin Standard Library at the time of writing this that accomplishes this,
 * so I came up with this. Specifically we needed to properly handle infinite sequences.
 */
internal fun <T : Comparable<T>>Sequence<T>.mergeWith(other: Sequence<T>, reverse: Boolean): Sequence<T> {
    fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null

    return sequence {
        val thisIter = iterator()
        val otherIter = other.iterator()

        var thisItemMut: T? = thisIter.nextOrNull()
        var otherItemMut: T? = otherIter.nextOrNull()

        while (thisItemMut != null || otherItemMut != null) {
            val thisItem = thisItemMut
            val otherItem = otherItemMut
            when {
                (thisItem != null && otherItem != null) -> {
                    if (thisItem.isWithinLimit(reverse, otherItem)) {
                        yield(thisItem)
                        thisItemMut = thisIter.nextOrNull()
                    } else {
                        yield(otherItem)
                        otherItemMut = otherIter.nextOrNull()
                    }
                }

                (otherItem != null) -> {
                    yield(otherItem)
                    otherItemMut = otherIter.nextOrNull()
                }
                (thisItem != null) -> {
                    yield(thisItem)
                    thisItemMut = thisIter.nextOrNull()
                }
            }
        }
    }
}

internal inline fun <T, R : Comparable<R>> Sequence<T>.sortedByReversible(
    reverse: Boolean,
    crossinline selector: (T) -> R
) = when (reverse) {
    true -> sortedByDescending(selector)
    false -> sortedBy(selector)
}

internal inline fun <T, R : Comparable<R>> List<T>.sortedByReversible(
    reverse: Boolean,
    crossinline selector: (T) -> R
) = when (reverse) {
    true -> sortedByDescending(selector)
    false -> sortedBy(selector)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T : Comparable<T>> T.isWithinLimit(reverse: Boolean, limit: T) = when (reverse) {
    true -> this >= limit
    false -> this <= limit
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T : Comparable<T>> T.isOutsideLimit(reverse: Boolean, limit: T) = when (reverse) {
    true -> this < limit
    false -> this > limit
}
