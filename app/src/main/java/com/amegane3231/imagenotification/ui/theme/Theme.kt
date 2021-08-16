package com.amegane3231.imagenotification.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Black,
    primaryVariant = BlackDark,
    secondary = LightBlue400,
    secondaryVariant = LightBlue700,
    onPrimary = White,
    onSecondary = Black
)

private val LightColorPalette = lightColors(
    primary = White,
    primaryVariant = WhiteDark,
    secondary = LightBlue400,
    secondaryVariant = LightBlue700,
    onPrimary = Black,
    onSecondary = White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ImageNotificationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
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