package com.oneeyedmen.anagrams

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnagramImplementationTests {

    @Test
    fun `could be made from the letters in`() {
        assertTrue("A".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("CAT".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("AA".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("ACT".couldBeMadeFromTheLettersIn("A CAT"))

        assertFalse("H".couldBeMadeFromTheLettersIn("A CAT"))
        assertFalse("AAH".couldBeMadeFromTheLettersIn("A CAT"))
        assertFalse("TAT".couldBeMadeFromTheLettersIn("A CAT"))

        assertTrue("".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("".couldBeMadeFromTheLettersIn(""))
    }

    @Test fun `letter bit sets`() {
        assertEquals("".toLetterBitSet(), "".toLetterBitSet())
        assertEquals(0, "".toLetterBitSet())
        assertEquals(1, "A".toLetterBitSet())
        assertEquals(1, "AA".toLetterBitSet())
        assertEquals(2, "B".toLetterBitSet())
        assertEquals(3, "BA".toLetterBitSet())

        assertFalse("A".toLetterBitSet() !in "A".toLetterBitSet())
        assertFalse("AA".toLetterBitSet() !in "A".toLetterBitSet())
        assertFalse("A".toLetterBitSet() !in "AA".toLetterBitSet())
        assertTrue("AB".toLetterBitSet() !in "A".toLetterBitSet())
        assertFalse("A".toLetterBitSet() !in "AB".toLetterBitSet())
    }

    @Test fun `empty WordInfo combinations`() {
        assertEquals(
            emptySet(),
            emptyList<WordInfo>().combinations()
        )
    }

    @Test fun `single WordInfo combinations`() {
        assertEquals(
            setOf("ACT", "CAT"),
            listOf(WordInfo(listOf("ACT", "CAT"))).combinations()
        )
    }

    @Test fun `WordInfo combinations`() {
        assertEquals(
            setOf("ACT TAB", "ACT BAT", "CAT TAB", "CAT BAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("TAB", "BAT"))
            ).combinations()
        )
        assertEquals(
            setOf("ACT A TAB", "ACT A BAT", "CAT A TAB", "CAT A BAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("A")),
                WordInfo(listOf("TAB", "BAT"))
            ).combinations()
        )
        val wordInfo = WordInfo(listOf("ACT", "CAT"))
        assertEquals(
            setOf("ACT A ACT", "ACT A CAT", "CAT A CAT"),
            listOf(
                wordInfo,
                WordInfo(listOf("A")),
                wordInfo
            ).combinations()
        )
    }
}

fun String.couldBeMadeFromTheLettersIn(letters: String): Boolean =
    WordInfo(listOf(this)).couldBeMadeFrom(Letters(letters.replace(" ", "")))

internal fun List<WordInfo>.combinations(): Set<String> = when {
    this.isEmpty() -> emptySet()
    else -> mutableListOf<String>().apply { permuteInto(this) }.toSet()
}
