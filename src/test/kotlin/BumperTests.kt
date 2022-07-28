import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.writeText

class BumperTests {
    @Test
    fun test() {
        val anagram = """ALTON CROOK REFITTING|REFRACTION KING LOTTO|REFACTORING TO KOTLIN"""
        renderAnagram(anagram)
    }
}

fun renderAnagram(anagram: String) {
    val tempDir = Files.createTempDirectory("bumper")
    val file = tempDir.resolve("html.html")
    file.writeText(getBodyString(anagram))
    Runtime.getRuntime().exec("open ${file}")
}


@Language("HTML")
fun getBodyString(anagram: String) = """
    <!DOCTYPE HTML>
    <html>
    <head>
        <link href='https://fonts.googleapis.com/css?family=Special+Elite' rel='stylesheet' type='text/css'>
        <link href="https://wordsmith.org/awad/style.css" type="text/css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"
                integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
        <script src="https://wordsmith.org/anagram/b64.js"></script>
        <script src="https://wordsmith.org/anagram/LZWEncoder.js"></script>
        <script src="https://wordsmith.org/anagram/NeuQuant.js"></script>
        <script src="https://wordsmith.org/anagram/GIFEncoder.js"></script>
        <script src="https://wordsmith.org/anagram/animation.min.js"></script>
    </head>

    <body>
    <blockquote>
        <div id="container_D7zp0"></div>
        <script>
        var animinput = {
            inputtext: "$anagram",
            animwidth: 640,
            animheight: 360,
            textcolor: "#FFFFFF",
            backgroundcolor: "#000000",
            font: "Special Elite",
            borderwidth: 0,
            bordercolor: "",
            cornerradius: 0,
            textshadow: 0,
            spacing: 0,
            fstyle: "normal",
            justify: "center",
            shadowcolor: "",
            shadowblur: 0,
            stroketext: 0,
            steps: 20,
            pause: 10,
            endpause: 0,
            verticalshift: 0,
            background_image: "",
            gifanimation: 1,
            container: "container_D7zp0"
        };
        animation(animinput);
        </script>
    </blockquote>
    </body>
    </html>
""".trimIndent()

