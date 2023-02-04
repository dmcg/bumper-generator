package com.oneeyedmen.anagrams

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
            input = SingleWordInfo(input.uppercase().replace(" ", "")),
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
                input = SingleWordInfo(remainingLetters),
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

internal fun WordInfo(words: List<String>) = when (words.size) {
    1 -> SingleWordInfo(words.first())
    0 -> error("")
    else -> MultiWordInfo(words)
}

internal sealed class WordInfo(val letterBitSet: Int) {
    abstract val words: List<String>
    abstract val exemplum: String

    fun couldBeMadeFromTheLettersIn(input: WordInfo) =
        !letterBitSet.hasLettersNotIn(input.letterBitSet) &&
                this.exemplum.couldBeMadeFromTheLettersIn(input.exemplum)

    fun minusLettersIn(other: WordInfo): String =
        this.exemplum.minusLettersIn(other.exemplum)
}

internal class SingleWordInfo(override val exemplum: String) : WordInfo(exemplum.toLetterBitSet()) {
    override val words: List<String>
        get() = listOf(exemplum)
}

internal class MultiWordInfo(override val words: List<String>) : WordInfo(words.first().toLetterBitSet()) {
    override val exemplum: String = words.first()
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