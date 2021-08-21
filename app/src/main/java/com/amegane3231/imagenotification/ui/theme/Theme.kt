package com.amegane3231.imagenotification.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalAppColors = staticCompositionLocalOf { lightColorPalette() }
private val LocalAppTypography = staticCompositionLocalOf { Typography }

@Composable
fun ImageNotificationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!darkTheme) {
        lightColorPalette()
    } else {
        darkColorPalette()
    }

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides Typography
    ) {
        MaterialTheme(
            colors = colors.materialColors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object ImageNotificationTheme {
    val colors: ColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = Typography
}