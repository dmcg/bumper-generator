package com.oneeyedmen.anagrams

import java.lang.StringBuilder

class Anagrams(words: List<String>) {

    private val wordInfos = words.sortedByDescending { it.length }
        .groupBy { String(it.toCharArray().sortedArray()) }
        .values
        .map { WordInfo(it) }

    fun anagramsFor(
        input: String,
        depth: Int = Int.MAX_VALUE,
    ): List<String> = anagramsFor(input, depth, instrumentation = {})

    internal fun anagramsFor(
        input: String,
        depth: Int = Int.MAX_VALUE,
        instrumentation: (MinusLettersInInvocation) -> Unit
    ): List<String> = mutableListOf<String>().apply {
        process(
            input = WordInfo(input.uppercase().replace(" ", "")),
            words = wordInfos,
            collector = { wordInfos -> addAll(wordInfos.combinations()) },
            depth = depth,
            instrumentation = instrumentation
        )
    }
}

private fun process(
    input: WordInfo,
    words: List<WordInfo>,
    collector: (List<WordInfo>) -> Unit,
    prefix: List<WordInfo> = emptyList(),
    depth: Int,
    instrumentation: (MinusLettersInInvocation) -> Unit = {}
) {
    val candidateWords = words.filter { wordInfo ->
        wordInfo.couldBeMadeFromTheLettersIn(input)
    }
    var remainingCandidateWords = candidateWords
    candidateWords.forEach { wordInfo ->
        instrumentation(MinusLettersInInvocation(input, wordInfo))
        val remainingLetters = input.minusLettersIn(wordInfo)
        when {
            remainingLetters.word.isEmpty() ->
                collector(prefix + wordInfo)

            depth > 1 -> process(
                input = remainingLetters,
                words = remainingCandidateWords,
                collector = collector,
                prefix = prefix + wordInfo,
                depth = depth - 1,
                instrumentation = instrumentation
            )
        }
        remainingCandidateWords = remainingCandidateWords.subList(
            1, remainingCandidateWords.size
        )
    }
}

internal data class MinusLettersInInvocation(
    val receiver: WordInfo, val parameter: WordInfo
)

internal class WordInfo(
    val words: List<String>,
    val letterBitSet: Int,
    val letterFrequencies: IntArray
) {
    val word: String = words.first()

    constructor(word: String) : this(listOf(word), word.toLetterBitSet(), word.toLetterFrequencies())
    constructor(words: List<String>) : this(words, words.first().toLetterBitSet(), words.first().toLetterFrequencies())

    fun couldBeMadeFromTheLettersIn(input: WordInfo) =
        !letterBitSet.hasLettersNotIn(input.letterBitSet) &&
                if (this.word.length > input.word.length)
                    false
                else
                    this.letterFrequencies.couldBeMadeFromTheLettersIn(input.letterFrequencies)

    fun minusLettersIn(other: WordInfo): WordInfo {
        val resultLetterFrequencies = this.letterFrequencies.copyOf()
        var resultLetterBitSet = 0
        val result = StringBuilder()
        for (index in resultLetterFrequencies.indices) {
            resultLetterFrequencies[index] -= other.letterFrequencies[index]
            resultLetterBitSet = resultLetterBitSet or (1 shl index)
            val char = 'A' + index
            for (i in 0 until resultLetterFrequencies[index]) {
                result.append(char)
            }
        }
        val resultAsString = result.toString()
        return WordInfo(listOf(resultAsString), resultLetterBitSet, resultLetterFrequencies)
    }
}

internal fun Int.hasLettersNotIn(other: Int) = (this and other) != this

internal fun String.toLetterBitSet(): Int {
    var result = 0
    this.forEach { char ->
        result = result or (1 shl char - 'A')
    }
    return result
}

internal fun String.toLetterFrequencies(): IntArray {
    val result = IntArray(26)
    this.forEach { char ->
        result[char - 'A']++
    }
    return result
}

internal fun String.couldBeMadeFromTheLettersIn(letters: String) = when {
    this.length > letters.length -> false
    else -> toLetterFrequencies().couldBeMadeFromTheLettersIn(letters.toLetterFrequencies())
}

private fun IntArray.couldBeMadeFromTheLettersIn(thatLetterFrequencies: IntArray): Boolean {
    forEachIndexed { index, frequency ->
        if (frequency > thatLetterFrequencies[index])
            return false
    }
    return true
}


private fun List<WordInfo>.combinations(): Set<String> =
    mutableListOf<String>().apply { permuteInto(this) }
        .map { it.split(" ").sorted().joinToString(" ") }
        .toSet()

internal fun List<WordInfo>.permuteInto(
    collector: MutableList<String>,
    prefix: String = ""
) {
    when (this.size) {
        0 -> {}
        1 -> this.first().words.forEach { word ->
            collector.add("$prefix $word".substring(1))
        }
        else -> this.first().words.forEach { word ->
            this.subList(1, this.size).permuteInto(collector, prefix = "$prefix $word")
        }
    }
}