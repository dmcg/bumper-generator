package com.oneeyedmen.anagrams

import com.oneeyedmen.okeydoke.Approver
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

val words: List<String> = File("./words.txt").readLines()

@Suppress("JUnitMalformedDeclaration")
@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class AnagramTests {

    @Test
    fun `01 could be made from the letters in`() {
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

    @Test
    fun `02 anagrams for A CAT`() {
        assertEquals(
            listOf("A ACT", "A CAT", "ACTA"),
            words.anagramsFor("A CAT", 3)
        )
    }

    @Test
    fun `03 anagrams for REFACTORING TO`(approver: Approver) {
        approver.assertApproved(
            words.anagramsFor("REFACTORING TO").joinToString("\n")
        )
    }

    @Test
    @EnabledIfSystemProperty(named = "run-slow-tests", matches = "true")
    fun `04 anagrams for REFACTORING TO KOTLIN depth 3`(approver: Approver) {
        approver.assertApproved(
            words.anagramsFor("REFACTORING TO KOTLIN", depth = 3).joinToString("\n")
        )
    }

    @Suppress("unused")
    companion object {
        @RegisterExtension
        @JvmField
        val approvals = ApprovalsExtension("src/test/kotlin")
    }
}

