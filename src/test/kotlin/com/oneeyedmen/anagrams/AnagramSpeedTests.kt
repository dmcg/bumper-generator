package com.oneeyedmen.anagrams

import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis


class AnagramSpeedTests {

    @Test
    fun `anagrams for REFACTORING TO`() {
        val input = "REFACTORING TO"
        val repetitions = 5
        val meanAndDeviation = (1..repetitions).map {
            measureTimeMillis {
                words.anagramsFor(input)
            }.toDouble()
        }.culledMeanAndDeviation()
        println("Duration ${meanAndDeviation.first.toLong()} ± ${meanAndDeviation.second.toLong()}")
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

