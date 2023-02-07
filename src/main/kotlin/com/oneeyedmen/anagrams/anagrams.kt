package com.oneeyedmen.anagrams

class AnagramGenerator(words: List<String>) {

    private val wordInfos = words.sortedByDescending { it.length }
        .groupBy { it.sortedLetters() }
        .values
        .mapIndexed { index, words -> WordInfo(words, index) }

    private val anagramsCache = mutableMapOf<Letters, MutableMap<Int, MutableMap<IntRange, List<WordTree>>>>()
    private val candidateWordsCache = mutableMapOf<Letters, List<WordInfo>>()

    fun anagramsFor(
        input: String,
        depth: Int = Int.MAX_VALUE,
        keepCache: Boolean = false,
    ): List<String> = anagramsFor(input, depth, keepCache, instrumentation = {})

    internal fun anagramsFor(
        input: String,
        depth: Int = Int.MAX_VALUE,
        keepCache: Boolean = false,
        instrumentation: (MinusLettersInInvocation) -> Unit,
    ): List<String> {
        val wordTrees = process(
            inputLetters = Letters(input.uppercase().replace(" ", "")),
            words = wordInfos,
            depth = depth,
            instrumentation = instrumentation
        )
        if (!keepCache) {
            anagramsCache.clear()
            candidateWordsCache.clear()
        }
        return wordTrees.anagrams()
    }

    private fun process(
        inputLetters: Letters,
        words: List<WordInfo>,
        index: Int = 0,
        depth: Int,
        instrumentation: (MinusLettersInInvocation) -> Unit = {}
    ): List<WordTree> {
        val candidateWords = candidateWordsCache.getOrPut(inputLetters) {
            words.filter { wordInfo ->
                wordInfo.couldBeMadeFrom(inputLetters)
            }
        }

        val firstIndex = candidateWords.indexOfFirst { word -> word.index >= index }
        if (firstIndex == candidateWords.size) {
            return emptyList()
        }
        val indexRange = (candidateWords.getOrNull(firstIndex - 1)?.index?.plus(1) ?: 0)..candidateWords[firstIndex].index

        return anagramsCache.getOrPut(inputLetters) { mutableMapOf() }.getOrPut(depth) { mutableMapOf() }.getOrPut(indexRange) {
            val result = mutableListOf<WordTree>()
            candidateWords.subListFrom(firstIndex).forEach { wordInfo ->
                instrumentation(MinusLettersInInvocation(inputLetters, wordInfo))
                val remainingLetters = inputLetters.minusLettersIn(wordInfo.word)
                when {
                    remainingLetters.isEmpty() ->
                        result.add(WordTree(wordInfo))

                    depth > 1 -> {
                        val wordResults = process(
                            inputLetters = remainingLetters,
                            words = candidateWords,
                            index = wordInfo.index,
                            depth = if (depth == Int.MAX_VALUE) depth else depth - 1,
                            instrumentation = instrumentation
                        )
                        if (wordResults.isNotEmpty()) {
                            result.add(WordTree(wordInfo, wordResults))
                        }
                    }
                }
            }
            result
        }
    }
}

internal class WordInfo(
    val words: List<String>,
    val index: Int = 0,
) {
    val word: String = words.first()
    private val letterBitSet: LetterBitSet = word.toLetterBitSet()

    fun couldBeMadeFrom(letters: Letters): Boolean {
        if (letterBitSet !in letters.letterBitSet || word.length > letters.length)
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
    val letterBitSet: LetterBitSet,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Letters

        if (length != other.length) return false
        if (letterBitSet != other.letterBitSet) return false
        if (!letterCounts.contentEquals(other.letterCounts)) return false

        return true
    }

    private val hashCode = letterCounts.contentHashCode()

    override fun hashCode(): Int = hashCode
}

private class WordTree(
    val wordInfo: WordInfo,
    val next: List<WordTree> = emptyList(),
)

private fun List<WordTree>.anagrams(): List<String> {
    val result = mutableListOf<String>()
    val anagram = mutableListOf<WordInfo>()

    fun visit(wordTrees: List<WordTree>) {
        when {
            wordTrees.isEmpty() -> result.addAll(anagram.combinations())
            else -> wordTrees.forEach { wordTree ->
                anagram.add(wordTree.wordInfo)
                visit(wordTree.next)
                anagram.removeLast()
            }
        }
    }

    visit(this)

    return result
}

typealias LetterBitSet = Int

operator fun LetterBitSet.contains(other: LetterBitSet): Boolean =
    (this and other) == other

internal fun String.toLetterBitSet(): LetterBitSet {
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

/**
 * Uses binary search to find index of first element that comply with the predicate.
 */
private fun <T> List<T>.indexOfFirst(predicate: (T) -> Boolean): Int {
    var from = -1
    var to = this.size
    while (from + 1 < to) {
        val m = (from + to) ushr 1
        if (predicate(this[m])) {
            to = m
        } else {
            from = m
        }
    }
    return to
}

// Just for instrumentation
internal data class MinusLettersInInvocation(
    val receiver: Letters, val parameter: WordInfo
)