package com.oneeyedmen.anagrams

class AnagramGenerator(words: List<String>) {

    private val wordInfos = words.sortedByDescending { it.length }
        .groupBy { it.sortedLetters() }
        .values
        .map { WordInfo(it) }

    fun anagramsFor(
        input: String,
        depth: Int = Int.MAX_VALUE,
    ): List<String> = anagramsFor(input, depth, instrumentation = {})

    internal fun anagramsFor(
        input: String,
        depth: Int = Int.MAX_VALUE,
        instrumentation: (MinusLettersInInvocation) -> Unit,
    ): List<String> {
        val result = mutableListOf<String>()
        process(
            inputLetters = Letters(input.uppercase().replace(" ", "")),
            words = wordInfos,
            collector = { wordInfos -> result.addAll(wordInfos.combinations()) },
            depth = depth,
            instrumentation = instrumentation
        )
        return result
    }
}

private fun process(
    inputLetters: Letters,
    words: List<WordInfo>,
    collector: (List<WordInfo>) -> Unit,
    prefix: MutableList<WordInfo> = mutableListOf(),
    depth: Int,
    instrumentation: (MinusLettersInInvocation) -> Unit = {}
) {
    val candidateWords = words.filter { wordInfo ->
        wordInfo.couldBeMadeFrom(inputLetters)
    }
    var remainingCandidateWords = candidateWords
    candidateWords.forEach { wordInfo ->
        instrumentation(MinusLettersInInvocation(inputLetters, wordInfo))
        val remainingLetters = inputLetters.minusLettersIn(wordInfo.word)
        prefix.add(wordInfo)
        when {
            remainingLetters.isEmpty() ->
                collector(prefix)

            depth > 1 -> process(
                inputLetters = remainingLetters,
                words = remainingCandidateWords,
                collector = collector,
                prefix = prefix,
                depth = depth - 1,
                instrumentation = instrumentation
            )
        }
        prefix.removeLast()
        remainingCandidateWords = remainingCandidateWords.subListFrom(1)
    }
}

internal class WordInfo(
    val words: List<String>,
) {
    val word: String = words.first()
    private val letterBitSet: Int = word.toLetterBitSet()

    fun couldBeMadeFrom(letters: Letters): Boolean {
        if (letterBitSet.hasLettersNotIn(letters.letterBitSet) || word.length > letters.length)
            return false
        val remainingLetterCounts = letters.letterCounts.copyOf()
        word.forEach { char ->
            val newCount = --remainingLetterCounts[char - 'A']
            if (newCount < 0)
                return false
        }
        return true
    }
}

internal class Letters(
    val length: Int,
    val letterBitSet: Int,
    val letterCounts: IntArray,
) {
    constructor(word: String) : this(
        word.length,
        word.toLetterBitSet(),
        IntArray(26).also { word.forEach { ch -> it[ch - 'A']++ } }
    )

    fun isEmpty(): Boolean = length == 0

    fun minusLettersIn(word: String): Letters {
        val remainingLetterCounts = this.letterCounts.copyOf()
        var remainingLetterBitSet = this.letterBitSet
        word.forEach { char ->
            val cnt = --remainingLetterCounts[char - 'A']
            if (cnt < 0)
                error("BAD")
            if (cnt == 0)
                remainingLetterBitSet = remainingLetterBitSet and (1 shl char - 'A').inv()
        }
        return Letters(
            this.length - word.length,
            remainingLetterBitSet,
            remainingLetterCounts
        )
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

internal fun List<WordInfo>.combinations(): Set<String> = when {
    this.isEmpty() -> emptySet()
    else -> mutableSetOf<String>().apply { permuteInto(this) }
}

private fun List<WordInfo>.permuteInto(
    collector: MutableSet<String>,
    prefix: MutableList<String> = mutableListOf()
) {
    when (this.size) {
        0 -> collector.add(prefix.sorted().joinToString(" "))
        else -> this.first().words.forEach { word ->
            prefix.add(word)
            this.subListFrom(1).permuteInto(collector, prefix = prefix)
            prefix.removeLast()
        }
    }
}

private fun String.sortedLetters() = String(toCharArray().apply { sort() })

private fun <T> List<T>.subListFrom(fromIndex: Int) = subList(fromIndex, size)

// Just for instrumentation
internal data class MinusLettersInInvocation(
    val receiver: Letters, val parameter: WordInfo
)