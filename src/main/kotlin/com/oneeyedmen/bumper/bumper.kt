package com.oneeyedmen.bumper

import com.madgag.gif.fmsware.AnimatedGifEncoder
import com.madgag.gif.fmsware.GifDecoder
import io.github.bonigarcia.wdm.WebDriverManager
import io.github.bonigarcia.wdm.managers.ChromeDriverManager
import org.intellij.lang.annotations.Language
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.*
import kotlin.io.path.writeText


fun renderAnagram(anagram: String, imageSpec: ImageSpec) {
    val file = Files.createTempDirectory("bumper").resolve("html.html")
    file.writeText(bodyStringToGenerateGifOf(anagram, imageSpec))
    val image = getGeneratedImageBytesFrom(file)
    val shorterImage = removeLastFewFramesFrom(image)
    File("gifs/$anagram.gif").writeBytes(shorterImage)
}

fun removeLastFewFramesFrom(image: ByteArray): ByteArray {
    val decoder = GifDecoder()
    decoder.read(ByteArrayInputStream(image))

    val result = ByteArrayOutputStream()
    val encoder = AnimatedGifEncoder().apply {
        start(result)
        setRepeat(-1)
    }


    for (i in 0..decoder.frameCount - 20) {
        encoder.addFrame(decoder.getFrame(i))
        if (i == 0 || i == 22)
            encoder.setDelay(1000)
        else
            encoder.setDelay(decoder.getDelay(i))
    }
    encoder.finish()
    return result.toByteArray()
}

private fun getGeneratedImageBytesFrom(file: Path): ByteArray {
    val option = ChromeOptions().apply {
        addArguments("--remote-allow-origins=*")
    }
    WebDriverManager.chromedriver().setup()
    val driver = ChromeDriver(option).apply {
        manage().timeouts().implicitlyWait(Duration.ofMinutes(1))
    }
    driver.get(file.toUri().toString())
    driver.get(file.toUri().toString())
    val base64ImageSrc = driver.findElement(By.tagName("img")).getAttribute("src")
    return Base64.getDecoder().decode(base64ImageSrc.substringAfter("base64,"))
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