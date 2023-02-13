package com.oneeyedmen.session

import java.io.File

private const val WORD_CNT = 10

fun main() {
    val sessionGenerator = AnagramGenerator(File("./words.txt").readLines())

    mainLoop@while (true) {
        println("Type letters for the anagram or empty string to exit:")
        val input = readlnOrNull()?.takeIf { it.isNotEmpty() } ?: break
        var trees = sessionGenerator.anagramsFor(input)
        if (trees.isEmpty()) {
            println("Anagrams are not founds.")
            continue
        }
        var words = ""
        while (trees.isNotEmpty()) {
            val variant = handleUserChoice(trees) ?: break@mainLoop
            trees = variant.tree.next
            words += variant.word + " "
            println("Selected words: $words")
        }
        println("Anagram: $words")
    }
}

private class Variant(
    val tree: WordTree,
    val word: String,
)

/**
 * Requests user to select next word and returns selected variant.
 * Returns first variant without request if there is only one available.
 * Returns null if user has chosen to exit.
 */
private fun handleUserChoice(trees: List<WordTree>): Variant? {
    val variants = trees.asSequence()
        .flatMap { tree ->
            tree.wordInfo.words.map { word ->
                Variant(tree, word)
            }
        }
        .take(WORD_CNT)
        .toList()
    if (variants.size == 1) {
        return variants.first()
    }
    println("There are following possible choices for the next word:")
    variants.forEachIndexed { index, variant ->
        println("${index + 1}. ${variant.word}")
    }
    println("Type index of the word or empty string to exit:")
    while (true) {
        val input = readlnOrNull()?.takeIf { it.isNotEmpty() } ?: return null
        val index = input.toIntOrNull()?.takeIf { it in 1..variants.size } ?: continue
        return variants[index - 1]
    }
}
