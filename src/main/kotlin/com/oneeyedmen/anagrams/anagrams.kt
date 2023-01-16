package com.oneeyedmen.anagrams

fun List<String>.anagramsFor(input: String, depth: Int = Int.MAX_VALUE): List<String> {
    val result = mutableListOf<String>()
    process(input.replace(" ", ""), this, { result.add(it) }, depth = depth)
    return result
}

private fun process(
    input: String,
    words: List<String>,
    collector: (String) -> Unit,
    prefix: String = "",
    depth: Int
) {
    val candidateWords = words.filter { it.couldBeMadeFromTheLettersIn(input) }
    var remainingCandidateWords = candidateWords
    candidateWords.forEach { word ->
        val remainingLetters = input.minusLettersIn(word)
        if (remainingLetters.isNotEmpty()) {
            if (depth > 1)
                process(remainingLetters, remainingCandidateWords, collector, prefix = "$prefix $word", depth - 1)
        } else {
            collector("$prefix $word".substring(1))
        }
        remainingCandidateWords = remainingCandidateWords.subList(1, remainingCandidateWords.size)
    }
}

internal fun String.couldBeMadeFromTheLettersIn(letters: String): Boolean {
    if (this.length > letters.length)
        return false
    val lettersAsList = letters.toMutableList()
    this.forEach { char ->
        if (!lettersAsList.remove(char))
            return false
    }
    return true
}

private fun String.minusLettersIn(word: String): String {
    val lettersAsList = this.toMutableList()
    word.forEach { char ->
        if (!lettersAsList.remove(char))
            error("BAD")
    }
    return String(lettersAsList.toCharArray())
}