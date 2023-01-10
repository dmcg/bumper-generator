import com.oneeyedmen.okeydoke.Approver
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

val words: List<String> = (File("./scrabble.txt")
    .readLines()
    .plus(listOf("A", "I", "O")).sorted())

class AnagramTests {

    @Test
    fun `could be made from the letters in`() {
        assertTrue("A".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("CAT".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("AA".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("ACT".couldBeMadeFromTheLettersIn("A CAT"))

        assertFalse("H".couldBeMadeFromTheLettersIn("A CAT"))
        assertFalse("AAH".couldBeMadeFromTheLettersIn("A CAT"))
        assertFalse("TAT".couldBeMadeFromTheLettersIn("A CAT"))

        assertTrue("".couldBeMadeFromTheLettersIn("A CAT"))
        assertTrue("".couldBeMadeFromTheLettersIn(""))
    }

    @Test
    fun `anagrams for A CAT`() {
        assertEquals(
            setOf("A ACT", "A CAT", "ACTA"),
            words.anagramsFor("A CAT")
        )
    }

    @Test
    fun `anagrams for ANAGRAM`(approver: Approver) {
        approver.assertApproved(
            words.anagramsFor("ANAGRAM").joinToString("\n")
        )
    }

    companion object {
        @RegisterExtension
        @JvmField
        val approvals = ApprovalsExtension("src/test/kotlin")
    }
}

fun List<String>.anagramsFor(input: String): Set<String> {
    val result = mutableListOf<String>()
    process(input, this, { result.add(it) })
    return result.map { it.split(" ").sorted().joinToString(" ") }.toSet()
}

private fun process(
    input: String,
    words: List<String>,
    collector: (String) -> Unit,
    prefix: String = ""
) {
    val candidateWords = words.filter { it.couldBeMadeFromTheLettersIn(input) }
    candidateWords.forEach { word ->
        val remainingLetters = input.minusLettersIn(word)
        if (remainingLetters.isNotBlank()) {
//            println("$prefix $word [$remainingLetters]")
            process(remainingLetters, candidateWords, collector, prefix = "$prefix $word")
        } else {
//            println("Found $prefix $word")
            collector("$prefix $word".substring(1))
        }
    }
}

private fun String.couldBeMadeFromTheLettersIn(letters: String): Boolean {
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

