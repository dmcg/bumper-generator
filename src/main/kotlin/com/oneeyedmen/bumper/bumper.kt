package com.oneeyedmen.bumper

import org.intellij.lang.annotations.Language
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

fun renderAnagram(anagram: String, imageSpec: ImageSpec) {
    val file = Files.createTempDirectory("bumper").resolve("html.html")
    file.writeText(bodyStringToGenerateGifOf(anagram, imageSpec))
    openBrowserOn(file)
}

private fun openBrowserOn(file: Path?) {
    Runtime.getRuntime().exec("open ${file}")
}

data class ImageSpec(
    val width: Int,
    val height: Int,
    val fontName: String,
    val textColor: Color,
    val backgroundColor: Color
)

@Language("HTML")
fun bodyStringToGenerateGifOf(anagram: String, imageSpec: ImageSpec) = """
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
            animwidth: ${imageSpec.width},
            animheight: ${imageSpec.height},
            textcolor: "${imageSpec.textColor.toCSS()}",
            backgroundcolor: "${imageSpec.backgroundColor.toCSS()}",
            font: "${imageSpec.fontName}",
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