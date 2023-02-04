package com.oneeyedmen.anagrams

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnagramImplementationTests {

    @Test
    fun `could be made from the letters in`() {
        assertTrue("A".couldBeMadeFromTheLettersIn("ACAT"))
        assertTrue("CAT".couldBeMadeFromTheLettersIn("ACAT"))
        assertTrue("AA".couldBeMadeFromTheLettersIn("ACAT"))
        assertTrue("ACT".couldBeMadeFromTheLettersIn("ACAT"))

        assertFalse("H".couldBeMadeFromTheLettersIn("ACAT"))
        assertFalse("AAH".couldBeMadeFromTheLettersIn("ACAT"))
        assertFalse("TAT".couldBeMadeFromTheLettersIn("ACAT"))

        assertTrue("".couldBeMadeFromTheLettersIn("ACAT"))
        assertTrue("".couldBeMadeFromTheLettersIn(""))
    }

    @Test fun `letter bit sets`() {
        assertEquals("".toLetterBitSet(), "".toLetterBitSet())
        assertEquals(0, "".toLetterBitSet())
        assertEquals(1, "A".toLetterBitSet())
        assertEquals(1, "AA".toLetterBitSet())
        assertEquals(2, "B".toLetterBitSet())
        assertEquals(3, "BA".toLetterBitSet())

        assertFalse("A".toLetterBitSet().hasLettersNotIn("A".toLetterBitSet()))
        assertFalse("AA".toLetterBitSet().hasLettersNotIn("A".toLetterBitSet()))
        assertFalse("A".toLetterBitSet().hasLettersNotIn("AA".toLetterBitSet()))
        assertTrue("AB".toLetterBitSet().hasLettersNotIn("A".toLetterBitSet()))
        assertFalse("A".toLetterBitSet().hasLettersNotIn("AB".toLetterBitSet()))
    }

    @Test fun `empty WordInfo permutations`() {
        assertEquals(
            emptyList(),
            emptyList<WordInfo>().permutations()
        )
    }

    @Test fun `single WordInfo permutations`() {
        assertEquals(
            listOf("ACT", "CAT"),
            listOf(WordInfo(listOf("ACT", "CAT"))).permutations()
        )
    }

    @Test fun `WordInfo permutations`() {
        assertEquals(
            listOf("ACT TAB", "ACT BAT", "CAT TAB", "CAT BAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("TAB", "BAT"))
            ).permutations()
        )
        assertEquals(
            listOf("ACT A TAB", "ACT A BAT", "CAT A TAB", "CAT A BAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("A")),
                WordInfo(listOf("TAB", "BAT"))
            ).permutations()
        )
    }
}

private fun List<WordInfo>.permutations(): List<String> {
    val collector = mutableListOf<String>()
    permuteInto(collector)
    return collector
}



