package com.oneeyedmen.anagrams

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class AnagramPropertyTests {

    @ParameterizedTest
    @ValueSource(strings = arrayOf( "racecar", "radar", "able was I" ))
    fun `has the same number of letters as the input`(input: String) {
        val anagrams = words.anagramsFor(input)
        anagrams.forEach { anagram ->
            assertEquals(input.withoutSpaces().length, anagram.withoutSpaces().length, anagram)
        }
    }
}

private fun String.withoutSpaces() = this.replace(" ", "")
