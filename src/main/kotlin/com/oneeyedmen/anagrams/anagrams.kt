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
    val candidateWords = words.filter {
        it.couldBeMadeFromTheLettersIn(input)
    }
    var remainingCandidateWords = candidateWords
    candidateWords.forEach { word ->
        val remainingLetters = input.minusLettersIn(word)
        if (remainingLetters.isNotEmpty()) {
            if (depth > 1)
                process(
                    input = remainingLetters,
                    words = remainingCandidateWords,
                    collector = collector, prefix = "$prefix $word",
                    depth = depth - 1
                )
        } else {
            collector("$prefix $word".substring(1))
        }
        remainingCandidateWords = remainingCandidateWords.subList(1, remainingCandidateWords.size)
    }
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
    return String(remainingLetters.filter { it != '*' }.toCharArray())
}