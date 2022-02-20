package com.example.jetpackcomposecodelab101.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.jetpackcomposecodelab101.ui.AppThemeState
import com.google.accompanist.systemuicontroller.SystemUiController

enum class ColorPalette {
    Bamboo,
    Coral,
    Fuji,
    Jade,
    Orenji,
    Pebble,
    Sakura,
    Sand,
    Sun,
    Wave,
}

// region Dark palettes

private val DarkBambooColorPalette = darkColors(
    primary = Bamboo3,
    primaryVariant = Bamboo4,
    secondary = Bamboo2,
    secondaryVariant = Bamboo3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkCoralColorPalette = darkColors(
    primary = Coral3,
    primaryVariant = Coral4,
    secondary = Coral2,
    secondaryVariant = Coral3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkFujiColorPalette = darkColors(
    primary = Fuji3,
    primaryVariant = Fuji4,
    secondary = Fuji2,
    secondaryVariant = Fuji3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkJadeColorPalette = darkColors(
    primary = Jade3,
    primaryVariant = Jade4,
    secondary = Jade2,
    secondaryVariant = Jade3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkOrenjiColorPalette = darkColors(
    primary = Orenji3,
    primaryVariant = Orenji4,
    secondary = Orenji2,
    secondaryVariant = Orenji3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkPebbleColorPalette = darkColors(
    primary = Pebble3,
    primaryVariant = Pebble4,
    secondary = Pebble2,
    secondaryVariant = Pebble3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkSakuraColorPalette = darkColors(
    primary = Sakura3,
    primaryVariant = Sakura4,
    secondary = Sakura2,
    secondaryVariant = Sakura3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkSandColorPalette = darkColors(
    primary = Sand3,
    primaryVariant = Sand4,
    secondary = Sand2,
    secondaryVariant = Sand3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkSunColorPalette = darkColors(
    primary = Sun3,
    primaryVariant = Sun4,
    secondary = Sun2,
    secondaryVariant = Sun3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

private val DarkWaveColorPalette = darkColors(
    primary = Wave3,
    primaryVariant = Wave4,
    secondary = Wave2,
    secondaryVariant = Wave3,
    background = Color.Black,
    surface = Color(0xFF121212),
    error = Color(0xFFB22222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color(0xFFFF2400)
)

// endregion

// region Light pallets

private val LightBambooColorPalette = lightColors(
    primary = Bamboo4,
    primaryVariant = Bamboo5,
    secondary = Bamboo1,
    secondaryVariant = Bamboo2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightCoralColorPalette = lightColors(
    primary = Coral4,
    primaryVariant = Coral5,
    secondary = Coral1,
    secondaryVariant = Coral2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightFujiColorPalette = lightColors(
    primary = Fuji4,
    primaryVariant = Fuji5,
    secondary = Fuji1,
    secondaryVariant = Fuji2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightJadeColorPalette = lightColors(
   primary = Jade4,
    primaryVariant = Jade5,
    secondary = Jade1,
    secondaryVariant = Jade2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightOrenjiColorPalette = lightColors(
    primary = Orenji4,
    primaryVariant = Orenji5,
    secondary = Orenji1,
    secondaryVariant = Orenji2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightPebbleColorPalette = lightColors(
    primary = Pebble4,
    primaryVariant = Pebble5,
    secondary = Pebble1,
    secondaryVariant = Pebble2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightSakuraColorPalette = lightColors(
    primary = Sakura4,
    primaryVariant = Sakura5,
    secondary = Sakura1,
    secondaryVariant = Sakura2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightSandColorPalette = lightColors(
    primary = Sand4,
    primaryVariant = Sand5,
    secondary = Sand1,
    secondaryVariant = Sand2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightSunColorPalette = lightColors(
    primary = Sun4,
    primaryVariant = Sun5,
    secondary = Sun1,
    secondaryVariant = Sun2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val LightWaveColorPalette = lightColors(
    primary = Wave4,
    primaryVariant = Wave5,
    secondary = Wave1,
    secondaryVariant = Wave2,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB22222),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

@Composable
fun JetpackComposeCodeLab101Theme(
    systemUiController: SystemUiController? = null,
    appThemeState: AppThemeState? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isDarkTheme = (appThemeState?.isSystemModeEnable == false
            && appThemeState.isDarkTheme) || darkTheme
    val colorPalette = appThemeState?.colorPalette ?: ColorPalette.Pebble
    val colors = getAppThemeColors(isDarkTheme, colorPalette)

    systemUiController?.apply {
        setStatusBarColor(color = colors.primary, darkIcons = darkTheme)
        setNavigationBarColor(color = colors.primary, darkIcons = darkTheme)
        setSystemBarsColor(color = colors.primary, darkIcons = darkTheme)
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

fun getAppThemeColors(
    isDarkTheme: Boolean,
    colorPalette: ColorPalette
): Colors {
    val colors = if (isDarkTheme) {
        when (colorPalette) {
            ColorPalette.Bamboo -> DarkBambooColorPalette
            ColorPalette.Coral -> DarkCoralColorPalette
            ColorPalette.Fuji -> DarkFujiColorPalette
            ColorPalette.Jade -> DarkJadeColorPalette
            ColorPalette.Orenji -> DarkOrenjiColorPalette
            ColorPalette.Pebble -> DarkPebbleColorPalette
            ColorPalette.Sakura -> DarkSakuraColorPalette
            ColorPalette.Sand -> DarkSandColorPalette
            ColorPalette.Sun -> DarkSunColorPalette
            ColorPalette.Wave -> DarkWaveColorPalette
        }
    } else {
        when (colorPalette) {
            ColorPalette.Bamboo -> LightBambooColorPalette
            ColorPalette.Coral -> LightCoralColorPalette
            ColorPalette.Fuji -> LightFujiColorPalette
            ColorPalette.Jade -> LightJadeColorPalette
            ColorPalette.Orenji -> LightOrenjiColorPalette
            ColorPalette.Pebble -> LightPebbleColorPalette
            ColorPalette.Sakura -> LightSakuraColorPalette
            ColorPalette.Sand -> LightSandColorPalette
            ColorPalette.Sun -> LightSunColorPalette
            ColorPalette.Wave -> LightWaveColorPalette
        }
    }
    return colors
}