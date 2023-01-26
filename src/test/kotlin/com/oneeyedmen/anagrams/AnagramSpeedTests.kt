package com.oneeyedmen.anagrams

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

@EnabledIfSystemProperty(named = "run-slow-tests", matches = "true")
@TestMethodOrder(MethodOrderer.MethodName::class)
class AnagramSpeedTests {

    @Test
    fun `anagrams for A WARMUP TEST`() {
        measureAndPrint("A WARMUP TEST", 10)
    }

    @Test
    fun `anagrams for REFACTORING`() {
        measureAndPrint("REFACTORING", 5)
    }

    @Test
    fun `anagrams for REFACTORING T`() {
        measureAndPrint("REFACTORING T", 5)
    }

    @Test
    fun `anagrams for REFACTORING TO`() {
        measureAndPrint("REFACTORING TO", 5)
    }

    @Test
    fun `anagrams for REFACTORING TO K`() {
        measureAndPrint("REFACTORING TO K", 5)
    }

    @Test
    fun `anagrams for REFACTORING TO KO`() {
        measureAndPrint("REFACTORING TO KO", 2)
    }

    @Test
    fun `anagrams for REFACTORING TO KOT`() {
        measureAndPrint("REFACTORING TO KOT", 1)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTL`() {
        measureAndPrint("REFACTORING TO KOTL", 1)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTLI`() {
        measureAndPrint("REFACTORING TO KOTLI", 1)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTLIN`() {
        measureAndPrint("REFACTORING TO KOTLIN", 1)
    }

    private fun measureAndPrint(input: String, repetitions: Int) {
        val resultsAndTimes = (1..repetitions).map {
            val results: List<String>
            val time = measureTimeMillis {
                results = words.anagramsFor(input)
            }.toDouble()
            results to time
        }
        val inputLength = input.replace(" ", "").length
        val resultsSize = resultsAndTimes.first().first.size
        val meanAndDeviation = resultsAndTimes.map { it.second }.culledMeanAndDeviation()
        println("$input: $inputLength letters gives $resultsSize results in ${meanAndDeviation.first.toLong()} ± ${meanAndDeviation.second.toLong()} ms")
    }
}

private fun List<Double>.culledMeanAndDeviation(): Pair<Double, Double> = when {
    isEmpty() -> Double.NaN to Double.NaN
    size == 1 || size == 2 -> this.meanAndDeviation()
    else -> sorted().subList(1, size - 1).meanAndDeviation()
}

private fun List<Double>.meanAndDeviation(): Pair<Double, Double> {
    val mean = sum() / size
    return mean to sqrt(fold(0.0) { acc, value -> acc + (value - mean).squared() } / size)
}

private fun Double.squared() = this * this

