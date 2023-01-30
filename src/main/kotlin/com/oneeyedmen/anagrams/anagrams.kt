package com.oneeyedmen.anagrams

fun List<String>.anagramsFor(
    input: String,
    depth: Int = Int.MAX_VALUE,
): List<String>  = this.anagramsFor(input, depth, instrumentation = {})

internal fun List<String>.anagramsFor(
    input: String,
    depth: Int = Int.MAX_VALUE,
    instrumentation: (MinusLettersInInvocation) -> Unit
): List<String> {
    val inputLongWordsFirst = this.sortedByDescending { it.length }
    val result = mutableListOf<String>()
    process(
        input = WordInfo(input.uppercase().replace(" ", "")),
        words = inputLongWordsFirst.map { word -> WordInfo(word) },
        collector = { result.add(it) },
        depth = depth,
        instrumentation = instrumentation
    )
    return result
}

private fun process(
    input: WordInfo,
    words: List<WordInfo>,
    collector: (String) -> Unit,
    prefix: String = "",
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
                collector("$prefix ${wordInfo.word}".substring(1))
            depth > 1 -> process(
                input = WordInfo(remainingLetters),
                words = remainingCandidateWords,
                collector = collector,
                prefix = "$prefix ${wordInfo.word}",
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
    val word: String,
    val letterBitSet: Int
) {
    constructor(word: String) : this(word, word.toLetterBitSet())

    fun couldBeMadeFromTheLettersIn(input: WordInfo) =
        !letterBitSet.hasLettersNotIn(input.letterBitSet) &&
                this.word.couldBeMadeFromTheLettersIn(input.word)

    fun minusLettersIn(other: WordInfo): String =
        this.word.minusLettersIn(other.word)
}

internal fun Int.hasLettersNotIn(other: Int) = (this and other) != this

internal fun String.toLetterBitSet(): Int {
    var result = 0
    this.forEach { char ->
        result = result or (1 shl char - 'A')
    }
    return result
}

internal fun String.couldBeMadeFromTheLettersIn(letters: String): Boolean {
    if (this.length > letters.length)
        return false
    val remainingLetters = letters.toCharArray()
    this.forEach { char ->
        val index = remainingLetters.indexOf(char)
        if (index == -1)
            return false
        remainingLetters[index] = '*'
    }
    return true
}

private fun String.minusLettersIn(word: String): String {
    val remainingLetters = this.toCharArray()
    word.forEach { char ->
        val index = remainingLetters.indexOf(char)
        if (index == -1)
            error("BAD")
        remainingLetters[index] = '*'
    }
    val result = CharArray(this.length - word.length)
    var index = 0
    remainingLetters.forEach { char ->
        if (char != '*')
            result[index++] = char
    }
    return String(result)
}