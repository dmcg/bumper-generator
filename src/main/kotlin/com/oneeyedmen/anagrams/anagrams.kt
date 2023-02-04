package com.oneeyedmen.anagrams

class Anagrams(words: List<String>) {

    private val wordInfos = words.sortedByDescending { it.length }
        .groupBy { it.withSortedCharacters() }
        .map { entry -> WordInfo(entry.key, entry.value) }

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
            val minusLettersInInvocation = MinusLettersInInvocation(input, wordInfo)


            val remainingLetters: String =
                cache.computeIfAbsent(minusLettersInInvocation) {
                    instrumentation(minusLettersInInvocation)
                    input.minusLettersIn(wordInfo)
                }
            when {
                remainingLetters.isEmpty() ->
                    collector(prefix + wordInfo)

                depth > 1 -> process(
                    input = WordInfo(remainingLetters.withSortedCharacters()),
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

    private val cache = mutableMapOf<MinusLettersInInvocation, String>()
}


internal data class MinusLettersInInvocation(
    val receiver: WordInfo, val parameter: WordInfo
)

internal class WordInfo(
    val alphabeticLetters: String,
    val words: List<String>,
    val letterBitSet: Int
) {
    val word: String = words.first()

    constructor(alphabeticLetters: String) : this(
        alphabeticLetters,
        listOf(alphabeticLetters),
        alphabeticLetters.toLetterBitSet()
    )

    constructor(alphabeticLetters: String, words: List<String>) : this(
        alphabeticLetters,
        words,
        alphabeticLetters.toLetterBitSet()
    )

    fun couldBeMadeFromTheLettersIn(input: WordInfo) =
        !letterBitSet.hasLettersNotIn(input.letterBitSet) &&
                this.word.couldBeMadeFromTheLettersIn(input.word)

    fun minusLettersIn(other: WordInfo): String =
        this.word.minusLettersIn(other.word)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WordInfo

        if (alphabeticLetters != other.alphabeticLetters) return false

        return true
    }

    override fun hashCode(): Int {
        return alphabeticLetters.hashCode()
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

private fun String.withSortedCharacters() = String(toCharArray().sortedArray())

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