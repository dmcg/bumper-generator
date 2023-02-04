package com.oneeyedmen.anagrams

class Anagrams(words: List<String>) {

    private val wordInfos = words.sortedByDescending { it.length }
        .groupBy { String(it.toCharArray().sortedArray()) }
        .values
        .map { WordInfo(it.map { it.toCharArray() }) }

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
            input = WordInfo(input.uppercase().replace(" ", "").toCharArray()),
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
            remainingLetters.isEmpty() ->
                collector(prefix + wordInfo)

            depth > 1 -> process(
                input = WordInfo(remainingLetters),
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
    val words: List<CharArray>,
    private val letterBitSet: Int
) {
    val word: CharArray = words.first()

    constructor(word: CharArray) : this(listOf(word), word.toLetterBitSet())
    constructor(words: List<CharArray>) : this(words, words.first().toLetterBitSet())

    fun couldBeMadeFromTheLettersIn(input: WordInfo) =
        !letterBitSet.hasLettersNotIn(input.letterBitSet) &&
                this.word.couldBeMadeFromTheLettersIn(input.word)

    fun minusLettersIn(other: WordInfo): CharArray =
        this.word.minusLettersIn(other.word)
}

internal fun Int.hasLettersNotIn(other: Int) = (this and other) != this

internal fun CharArray.toLetterBitSet(): Int {
    var result = 0
    this.forEach { char ->
        result = result or (1 shl char - 'A')
    }
    return result
}

internal fun CharArray.couldBeMadeFromTheLettersIn(letters: CharArray): Boolean {
    if (this.size > letters.size)
        return false
    val remainingLetters = letters.copyOf()
    this.forEach { char ->
        val index = remainingLetters.indexOf(char)
        if (index == -1)
            return false
        remainingLetters[index] = '*'
    }
    return true
}

private fun CharArray.minusLettersIn(word: CharArray): CharArray {
    val remainingLetters = this.copyOf()
    word.forEach { char ->
        val index = remainingLetters.indexOf(char)
        if (index == -1)
            error("BAD")
        remainingLetters[index] = '*'
    }
    val result = CharArray(this.size - word.size)
    var index = 0
    remainingLetters.forEach { char ->
        if (char != '*')
            result[index++] = char
    }
    return result
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
            collector.add("$prefix ${String(word)}".substring(1))
        }
        else -> this.first().words.forEach { word ->
            this.subList(1, this.size).permuteInto(collector, prefix = "$prefix ${String(word)}")
        }
    }
}