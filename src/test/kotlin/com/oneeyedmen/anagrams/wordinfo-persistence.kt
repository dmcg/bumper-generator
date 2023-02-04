package com.oneeyedmen.anagrams

import java.io.File

fun main() {
    val words: List<String> = File("./words.txt").readLines()
    val anagrams = anagrams(words)

    val longestWordLength = words.maxBy { it.length }.length
    words.filter {it.length == longestWordLength}.forEach(::println)

    File("./wordinfos.txt").bufferedWriter().use { writer ->
        anagrams.wordInfos.forEach { wordInfo ->
            writer.appendLine(wordInfo.toLine())
        }
    }

    val read = File("./wordinfos.txt").useLines { lines ->
        lines.map { line ->
            val parts = line.split('\t')
            WordInfo(parts[1].split(" "), parts[0].toInt())
        }.toList()
    }

}



private fun WordInfo.toLine() = letterBitSet.toString() + '\t' + words.joinToString(" ")
