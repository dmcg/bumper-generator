import com.oneeyedmen.bumper.ImageSpec
import com.oneeyedmen.bumper.renderAnagram
import org.junit.jupiter.api.Test
import java.awt.Color.*

class BumperTests {
    @Test
    fun test() {
        val anagram = """SKITTISH RAMJET TRUNK|THINK MUSKRAT JITTERS|JUNIT HAMKREST STRIKT"""
        renderAnagram(
            anagram,
            ImageSpec(
                width = 720,
                height = 480,
                fontName = "Courier",
                textColor = WHITE,
                backgroundColor = BLACK,
            )
        )
    }
}


