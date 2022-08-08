import com.oneeyedmen.bumper.ImageSpec
import com.oneeyedmen.bumper.renderAnagram
import org.junit.jupiter.api.Test
import java.awt.Color.*

class BumperTests {
    @Test
    fun test() {
        val anagram = """LIVE|EVIL"""
        renderAnagram(
            anagram,
            ImageSpec(
                width = 360,
                height = 240,
                fontName = "Arial",
                textColor = BLUE,
                backgroundColor = BLACK,
            )
        )
    }
}


