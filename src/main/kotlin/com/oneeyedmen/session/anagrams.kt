package com.oneeyedmen.session

class AnagramGenerator(words: List<String>) {

    private val wordInfos = words
        .groupBy { it.sortedLetters() }
        .values
        .map { words -> WordInfo(words) }
        .sortedByDescending { it.word.length }

    private val anagramsCache = mutableMapOf<Letters, List<WordTree>>()

    internal fun anagramsFor(
        input: String,
        keepCache: Boolean = false,
    ): List<WordTree> {
        val wordTrees = process(
            inputLetters = Letters(input.uppercase().replace(" ", "")),
            words = wordInfos,
        )
        if (!keepCache) {
            anagramsCache.clear()
        }
        return wordTrees
    }

    private fun process(
        inputLetters: Letters,
        words: List<WordInfo>,
    ): List<WordTree> = anagramsCache.getOrPut(inputLetters) {
        val candidateWords = words.filter { wordInfo ->
            wordInfo.couldBeMadeFrom(inputLetters)
        }
        val result = mutableListOf<WordTree>()
        candidateWords.forEach { wordInfo ->
            val remainingLetters = inputLetters.minusLettersIn(wordInfo.word)
            if (remainingLetters.isEmpty()) {
                result.add(WordTree(wordInfo))
            }
            val wordResults = process(
                inputLetters = remainingLetters,
                words = candidateWords,
            )
            if (wordResults.isNotEmpty()) {
                result.add(WordTree(wordInfo, wordResults))
            }
        }
        result
    }
}

internal class WordInfo(
    val words: List<String>,
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

internal class WordTree(
    val wordInfo: WordInfo,
    val next: List<WordTree> = emptyList(),
)

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

private fun String.sortedLetters() = String(toCharArray().apply { sort() })
