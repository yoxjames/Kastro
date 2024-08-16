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

import kotlin.test.Test
import kotlin.test.assertEquals

class SequenceExtTest {
    @Test
    fun TestMergeComparableSequences() {
        val sequence1 = sequenceOf(1,2,3,4,4,5,6,7,8,15)
        val sequence2 = sequenceOf(10,20,30,40,50,60,70)

        val expected = listOf(1,2,3,4,4,5,6,7,8,10,15,20,30,40,50,60,70)
        assertEquals(
            expected = expected,
            actual = sequence1.mergeWith(other = sequence2, reverse = false).toList()
        )
    }

    @Test
    fun TestMergeComparableSequencesReverse() {
        val sequence1 = sequenceOf(15,8,7,6,5,4,4,3,2,1)
        val sequence2 = sequenceOf(70,60,50,40,30,20,10)

        val expected = listOf(70,60,50,40,30,20,15,10,8,7,6,5,4,4,3,2,1)
        assertEquals(
            expected = expected,
            actual = sequence1.mergeWith(other = sequence2, reverse = true).toList()
        )
    }

    @Test
    fun TestMergeSequencesWhereFirstIsEmpty() {
        val sequence1 = sequenceOf<Int>()
        val sequence2 = sequenceOf(10,20,30,40,50,60,70)

        val expected = listOf(10,20,30,40,50,60,70)
        assertEquals(
            expected = expected,
            actual = sequence1.mergeWith(sequence2, reverse = false).toList()
        )
    }

    @Test
    fun TestMergeSequencesWhereFirstIsEmptyReverse() {
        val sequence1 = sequenceOf<Int>()
        val sequence2 = sequenceOf(70,60,50,40,30,20,10)

        val expected = listOf(70,60,50,40,30,20,10)
        assertEquals(
            expected = expected,
            actual = sequence1.mergeWith(sequence2, reverse = true).toList()
        )
    }

    @Test
    fun TestMergeSequencesWhereSecondIsEmpty() {
        val sequence1 = sequenceOf(10,20,30,40,50,60,70)
        val sequence2 = sequenceOf<Int>()

        val expected = listOf(10,20,30,40,50,60,70)
        assertEquals(
            expected = expected,
            actual = sequence1.mergeWith(sequence2, reverse = false).toList()
        )
    }

    @Test
    fun TestMergeSequencesWhereSecondIsEmptyReverse() {
        val sequence1 = sequenceOf(70,60,50,40,30,20,10)
        val sequence2 = sequenceOf<Int>()

        val expected = listOf(70,60,50,40,30,20,10)
        assertEquals(
            expected = expected,
            actual = sequence1.mergeWith(sequence2, reverse = true).toList()
        )
    }
}