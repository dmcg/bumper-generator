package com.oneeyedmen.bumper

import java.awt.Color

fun Color.toCSS(): String {
    val red = Integer.toHexString(red).prefixZero()
    val green = Integer.toHexString(green).prefixZero()
    val blue = Integer.toHexString(blue).prefixZero()
    return "#$red$green$blue"
}

private fun String.prefixZero(): String =
    when (length) {
        2 -> this
        1 -> "0$this"
        else -> error("Too many hex digits")
    }