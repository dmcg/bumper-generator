package com.oneeyedmen.session

import com.oneeyedmen.anagrams.culledMeanAndDeviation
import com.oneeyedmen.anagrams.words
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.system.measureTimeMillis

private val sessionGenerator = AnagramGenerator(words)

@TestMethodOrder(MethodOrderer.MethodName::class)
class SessionSpeedTests {

    @Test
    fun `anagrams for REFACTORING`() {
        report("REFACTORING", repetitions = 50)
    }

    @Test
    fun `anagrams for REFACTORING T`() {
        report("REFACTORING T", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO`() {
        report("REFACTORING TO", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO K`() {
        report("REFACTORING TO K", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO KO`() {
        report("REFACTORING TO KO", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO KOT`() {
        report("REFACTORING TO KOT", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTL`() {
        report("REFACTORING TO KOTL", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTLI`() {
        report("REFACTORING TO KOTLI", repetitions = 10)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTLIN`() {
        report("REFACTORING TO KOTLIN", repetitions = 10)
    }

    private fun report(input: String, repetitions: Int) {
        val timeAndResultCounts = (1..repetitions).map {
            System.gc()
            System.runFinalization()
            measureTimeMillis {
                sessionGenerator.anagramsFor(input)
            }
        }
        val meanAndDeviation = timeAndResultCounts
            .map { it.toDouble() }
            .culledMeanAndDeviation()
        println(
            "$input : Duration ${meanAndDeviation.first.toLong()} ± ${meanAndDeviation.second.toLong()}"
        )
    }
}
