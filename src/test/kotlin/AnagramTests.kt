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
            words.anagramsFor("A CAT", 3)
        )
    }

    @Test
    fun `anagrams for ANAGRAM`(approver: Approver) {
        approver.assertApproved(
            words.anagramsFor("ANAGRAM", 3).joinToString("\n")
        )
    }

    @Test
    fun `anagrams for REFACTORING`(approver: Approver) {
        approver.assertApproved(
            words.anagramsFor("REFACTORING").joinToString("\n")
        )
    }

    @Test
    fun `anagrams for REFACTORING TO KOTLIN depth 3`(approver: Approver) {
        approver.assertApproved(
            words.anagramsFor("REFACTORING TO KOTLIN", depth = 3).joinToString("\n")
        )
    }

    companion object {
        @RegisterExtension
        @JvmField
        val approvals = ApprovalsExtension("src/test/kotlin")
    }
}

fun List<String>.anagramsFor(input: String, depth: Int = Int.MAX_VALUE): Set<String> {
    val result = mutableListOf<String>()
    process(input.replace(" ", ""), this, { result.add(it) }, depth = depth)
    return result.map { it.split(" ").sorted().joinToString(" ") }.toSet()
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

