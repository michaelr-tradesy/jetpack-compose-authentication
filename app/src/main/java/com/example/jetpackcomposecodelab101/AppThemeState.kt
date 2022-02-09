package com.example.jetpackcomposecodelab101

import com.example.jetpackcomposecodelab101.ui.theme.ColorPalette

interface AppThemeState {
    val isDarkTheme: Boolean
    val colorPalette: ColorPalette
}

data class DefaultAppThemeState(
    override var isDarkTheme: Boolean = true,
    override var colorPalette: ColorPalette = ColorPalette.Bamboo
) : AppThemeState