package com.oneeyedmen.anagrams

import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnagramPropertyTests {

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("inputs")
    fun `has the same number of letters as the input`(input: String, anagrams: List<String>) {
        val withoutSpaces = input.withoutSpaces()
        anagrams.forEach { anagram ->
            assertEquals(
                withoutSpaces.length,
                anagram.withoutSpaces().length,
                "$anagram has the wrong number of letters"
            )
        }
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("inputs")
    fun `all the words are in the dictionary`(input: String, anagrams: List<String>) {
        val wordsAsSet = words.toSet()
        anagrams.forEach { anagram ->
            anagram.split(' ').forEach { word ->
                assertTrue(
                    wordsAsSet.contains(word),
                    "$word from $anagram is not in dictionary"
                )
            }
        }
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("inputs")
    fun `input and anagrams have the same letters`(input: String, anagrams: List<String>) {
        val inputLetters = input.lettersInAlphabeticalOrder()
        anagrams.forEach { anagram ->
            assertEquals(
                inputLetters,
                anagram.lettersInAlphabeticalOrder(),
                "$anagram does not have the right letters"
            )
        }
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("inputs")
    fun `one anagram contains input words`(input: String, anagrams: List<String>) {
        assumeTrue(input.isNotBlank())
        val inputSorted = input.uppercase().wordsInAlphabeticalOrder()
        assertEquals(
            1,
            anagrams.filter { it.wordsInAlphabeticalOrder() == inputSorted }.size
        )
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("inputs")
    fun `anagrams are unique`(input: String, anagrams: List<String>) {
        val normalizedAnagrams = anagrams.map { it.wordsInAlphabeticalOrder().joinToString(" ") }
        assertEquals(
            normalizedAnagrams.toSet().joinToString("\n"),
            normalizedAnagrams.joinToString("\n")
        )
    }

    fun inputs() = inputs

    private val pathologicals = listOf("a", "")
    private val upToThreeLetterWords = words.filter { it.length < 4 }
    private val shortWords = words.filter { it.length < 6 }
    private val randomShortWords = (1..30).map { shortWords.random() }
    private val randomWordPairs = (1..30).map {
        shortWords.random() + " " +
                shortWords.random()
    }
    private val randomWordTriples = (1..30).map {
        upToThreeLetterWords.random() + " " +
                shortWords.random() + " " +
                upToThreeLetterWords.random()
    }
    private val inputs = (pathologicals + randomShortWords + randomWordPairs + randomWordTriples).map {
        arguments(it, generator.anagramsFor(it))
    }
}

private fun String.withoutSpaces() = this.replace(" ", "")
private fun String.wordsInAlphabeticalOrder() = split(' ').sorted()
private fun String.lettersInAlphabeticalOrder() = withoutSpaces().uppercase().toCharArray().sorted()

