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

        assertFalse("A".toLetterBitSet().hasLettersNotIn("A".toLetterBitSet()))
        assertFalse("AA".toLetterBitSet().hasLettersNotIn("A".toLetterBitSet()))
        assertFalse("A".toLetterBitSet().hasLettersNotIn("AA".toLetterBitSet()))
        assertTrue("AB".toLetterBitSet().hasLettersNotIn("A".toLetterBitSet()))
        assertFalse("A".toLetterBitSet().hasLettersNotIn("AB".toLetterBitSet()))
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
            setOf("ACT TAB", "ACT BAT", "CAT TAB", "BAT CAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("TAB", "BAT"))
            ).combinations()
        )
        assertEquals(
            setOf("A ACT TAB", "A ACT BAT", "A CAT TAB", "A BAT CAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("A")),
                WordInfo(listOf("TAB", "BAT"))
            ).combinations()
        )
        assertEquals(
            setOf("A ACT ACT", "A ACT CAT", "A CAT CAT"),
            listOf(
                WordInfo(listOf("ACT", "CAT")),
                WordInfo(listOf("A")),
                WordInfo(listOf("ACT", "CAT"))
            ).combinations()
        )
    }
}
