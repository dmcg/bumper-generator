package com.oneeyedmen.anagrams

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals


@TestMethodOrder(MethodOrderer.MethodName::class)
class AnagramSpeedTests {

    @Test
    fun `anagrams for REFACTORING`() {
        report("REFACTORING", repetitions = 50, expectedResultCount = 9017)
    }

    @Test
    fun `anagrams for REFACTORING TO`() {
        report("REFACTORING TO", repetitions = 10, expectedResultCount = 128270)
    }
}

private fun report(input: String, repetitions: Int, expectedResultCount: Int) {
    val timeAndResultCounts = (1..repetitions).map {
        val resultCount: Int
        val timeMs = measureTimeMillis {
            resultCount = words.anagramsFor(input).size
        }
        resultCount to timeMs
    }
    val meanAndDeviation = timeAndResultCounts
        .map { it.second.toDouble() }
        .culledMeanAndDeviation()
    println(
        "Duration ${meanAndDeviation.first.toLong()} ± ${meanAndDeviation.second.toLong()}"
    )

    timeAndResultCounts.forEach { (count, _) -> assertEquals(expectedResultCount, count) }
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

