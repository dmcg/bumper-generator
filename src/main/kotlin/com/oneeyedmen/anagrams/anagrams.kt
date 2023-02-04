package com.oneeyedmen.anagrams

fun List<String>.anagramsFor(
    input: String,
    depth: Int = Int.MAX_VALUE,
): List<String> = this.anagramsFor(input, depth, instrumentation = {})

internal fun List<String>.anagramsFor(
    input: String,
    depth: Int = Int.MAX_VALUE,
    instrumentation: (MinusLettersInInvocation) -> Unit
): List<String> {
    val groups = this
        .sortedByDescending { it.length }
        .groupBy { String(it.toCharArray().sortedArray()) }
        .values
        .map { WordInfo(it) }
    val result = mutableListOf<String>()
    process(
        input = WordInfo(input.uppercase().replace(" ", "")),
        words = groups,
        collector = { wordInfos -> result.addAll(wordInfos.combinations()) },
        depth = depth,
        instrumentation = instrumentation
    )
    return result
}

private fun process(
    input: WordInfo,
    words: List<WordInfo>,
    collector: (List<WordInfo>) -> Unit,
    prefix: MutableList<WordInfo> = mutableListOf(),
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
        prefix.add(wordInfo)
        when {
            remainingLetters.isEmpty() ->
                collector(prefix)

            depth > 1 -> process(
                input = WordInfo(remainingLetters),
                words = remainingCandidateWords,
                collector = collector,
                prefix = prefix,
                depth = depth - 1,
                instrumentation = instrumentation
            )
        }
        prefix.removeLast()
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
    val letterBitSet: Int
) {
    val word: String = words.first()

    constructor(word: String) : this(listOf(word), word.toLetterBitSet())
    constructor(words: List<String>) : this(words, words.first().toLetterBitSet())

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


internal fun List<WordInfo>.combinations(): Set<String> {
    if (this.isEmpty()) return emptySet()
    return mutableSetOf<String>().apply { permuteInto(this) }
}

private fun List<WordInfo>.permuteInto(
    collector: MutableSet<String>,
    prefix: String = ""
) {
    when (this.size) {
        0 -> collector.add(prefix.substring(1).split(" ").sorted().joinToString(" "))
        else -> this.first().words.forEach { word ->
            this.subList(1, this.size).permuteInto(collector, prefix = "$prefix $word")
        }
    }
}