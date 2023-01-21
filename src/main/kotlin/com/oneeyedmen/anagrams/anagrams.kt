package com.oneeyedmen.anagrams

fun List<String>.anagramsFor(input: String, depth: Int = Int.MAX_VALUE): List<String> {
    val result = mutableListOf<String>()
    process(
        input = Letters(input.replace(" ", "")),
        words = this.map { word -> WordInfo(word) },
        collector = { result.add(it) },
        depth = depth
    )
    return result
}

private fun process(
    input: Letters,
    words: List<WordInfo>,
    startingIndex: Int = 0,
    collector: (String) -> Unit,
    prefix: String = "",
    depth: Int
) {
    val candidateWords = mutableListOf<WordInfo>()
    val wordsAndRemainingLetters = mutableListOf<Pair<WordInfo, Letters>>()
    main@ for (i in startingIndex until words.size) {
        val wordInfo = words[i]
        if (wordInfo.letterBitSet.hasLettersNotIn(input.letterBitSet)) {
            continue
        }
        val remainingLetterCounts = input.letterCounts.copyOf()
        var remainingLetterBitSet = input.letterBitSet
        for (char in wordInfo.word) {
            val cnt = --remainingLetterCounts[char - 'A']
            if (cnt < 0)
                continue@main
            if (cnt == 0)
                remainingLetterBitSet = remainingLetterBitSet and (1 shl char - 'A').inv()
        }
        val remainingLetters = Letters(
            input.length - wordInfo.word.length,
            remainingLetterBitSet,
            remainingLetterCounts
        )
        wordsAndRemainingLetters.add(wordInfo to remainingLetters)
        candidateWords.add(wordInfo)
    }
    wordsAndRemainingLetters.forEachIndexed { index, (wordInfo, remainingLetters) ->
        when {
            remainingLetters.length == 0 ->
                collector("$prefix ${wordInfo.word}".substring(1))
            depth > 1 -> process(
                input = remainingLetters,
                words = candidateWords,
                startingIndex = index,
                collector = collector,
                prefix = "$prefix ${wordInfo.word}",
                depth = depth - 1
            )
        }
    }
}

private class WordInfo(
    val word: String,
) {
    val letterBitSet: Int = word.toLetterBitSet()
}

private class Letters(
    val length: Int,
    val letterBitSet: Int,
    val letterCounts: IntArray,
) {
    constructor(word: String) : this(
        word.length,
        word.toLetterBitSet(),
        IntArray(26).also { word.forEach { ch -> it[ch - 'A']++ } }
    )
}

internal fun Int.hasLettersNotIn(other: Int) = (this and other) != this

internal fun String.toLetterBitSet(): Int {
    var result = 0
    this.forEach { char ->
        result = result or (1 shl char - 'A')
    }
    return result
}