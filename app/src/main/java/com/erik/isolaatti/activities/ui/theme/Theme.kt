package com.erik.isolaatti.activities.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xff5c0075),
    secondary = Color(0xff5c0075)
)

private val LightColorPalette = lightColors(
    primary = Color(0xff5c0075),
    background = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    onPrimary = Color.White
)

@Composable
fun IsolaattiTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}