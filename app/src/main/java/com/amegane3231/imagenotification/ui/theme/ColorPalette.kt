package com.amegane3231.imagenotification.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

interface ColorPalette {
    val primary: Color
    val background: Color
    val basic: Color
    val disable: Color
    val text: Color
    val success: Color
    val link: Color
    val warning: Color
    val error: Color
    val materialColors: Colors
}

fun lightColorPalette(): ColorPalette = object : ColorPalette {
    override val primary: Color = AppColors.primary
    override val background: Color = AppColors.background
    override val basic: Color = AppColors.basic
    override val disable: Color = AppColors.disable
    override val text: Color = AppColors.text
    override val success: Color = AppColors.success
    override val link: Color = AppColors.link
    override val warning: Color = AppColors.warning
    override val error: Color = AppColors.error
    override val materialColors: Colors = lightColors(
        primary = AppColors.primary
    )
}

fun darkColorPalette(): ColorPalette = object : ColorPalette {
    override val primary: Color = AppColors.primary
    override val background: Color = AppColors.background
    override val basic: Color = AppColors.basic
    override val disable: Color = AppColors.disable
    override val text: Color = AppColors.text
    override val success: Color = AppColors.success
    override val link: Color = AppColors.link
    override val warning: Color = AppColors.warning
    override val error: Color = AppColors.error
    override val materialColors: Colors = darkColors(
        primary = AppColors.primary
    )
}