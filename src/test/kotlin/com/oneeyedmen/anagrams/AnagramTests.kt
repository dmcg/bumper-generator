package com.oneeyedmen.anagrams

import com.oneeyedmen.okeydoke.Approver
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File
import kotlin.test.assertEquals

val words: List<String> = File("./words.txt").readLines()
val generator = AnagramGenerator(words)

@Suppress("JUnitMalformedDeclaration")
class AnagramTests {

    @Test
    fun `anagrams for A CAT`() {
        val expected = listOf("ACTA", "ACT A", "CAT A")
        assertEquals(
            expected,
            generator.anagramsFor("A CAT")
        )
        assertEquals(
            expected,
            generator.anagramsFor("a cat")
        )
    }

    @Test
    fun `anagrams for REFACTORING TO`(approver: Approver) {
        approver.assertApproved(
            generator.anagramsFor("REFACTORING TO").joinToString("\n")
        )
    }

    @Test
    @EnabledIfSystemProperty(named = "run-slow-tests", matches = "true")
    fun `anagrams for REFACTORING TO KOTLIN depth 3`(approver: Approver) {
        approver.assertApproved(
            generator.anagramsFor("REFACTORING TO KOTLIN", depth = 3).joinToString("\n")
        )
    }

    @Suppress("unused")
    companion object {
        @RegisterExtension
        @JvmField
        val approvals = ApprovalsExtension("src/test/kotlin")
    }
}