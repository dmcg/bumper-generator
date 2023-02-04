package com.oneeyedmen.anagrams

import org.junit.jupiter.api.Disabled
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
    fun `anagrams for REFACTORING T`() {
        report("REFACTORING T", repetitions = 10, expectedResultCount = 15108)
    }

    @Test
    fun `anagrams for REFACTORING TO`() {
        report("REFACTORING TO", repetitions = 10, expectedResultCount = 128270)
    }

    @Test
    fun `anagrams for REFACTORING TO K`() {
        report("REFACTORING TO K", repetitions = 1, expectedResultCount = 222225)
    }

    @Test
    fun `anagrams for REFACTORING TO KO`() {
        report("REFACTORING TO KO", repetitions = 1, expectedResultCount = 1038259)
    }

    @Test
    fun `anagrams for REFACTORING TO KOT`() {
        report("REFACTORING TO KOT", repetitions = 1, expectedResultCount = 1609238)
    }

    @Test
    fun `anagrams for REFACTORING TO KOTL`() {
        report("REFACTORING TO KOTL", repetitions = 1, expectedResultCount = 5412104)
    }

    @Disabled
    @Test
    fun `anagrams for REFACTORING TO instrumented`() {
        val invocations = mutableListOf<MinusLettersInInvocation>()
        words.anagramsFor(
            "REFACTORING TO",
            instrumentation = { invocations.add(it) }
        )
        val invocationCounts: Map<Pair<Int, Int>, Int> = invocations.map { invocation ->
            invocation.receiver.word.length to invocation.parameter.word.length
        }.groupingBy { it }.eachCount()
        println("Receiver Size, Parameter Size, Invocation Count")
        invocationCounts.forEach { (entry, count) ->
            println("${entry.first}, ${entry.second}, $count")
        }
    }
}

private fun report(input: String, repetitions: Int, expectedResultCount: Int = -1) {
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
    val resultCount = timeAndResultCounts.first().first
    println(
        "$input : Duration ${meanAndDeviation.first.toLong()} ± ${meanAndDeviation.second.toLong()}, Result count $resultCount"
    )
    if (expectedResultCount != -1)
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

