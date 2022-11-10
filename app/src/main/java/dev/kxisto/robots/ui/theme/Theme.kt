package dev.kxisto.robots.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = AppColors.PrimaryDark,
    primaryVariant = AppColors.PrimaryVariantDark,
    secondary = AppColors.SecondaryDark,
    background = AppColors.BackgroundDark,
)

private val LightColorPalette = lightColors(
    primary = AppColors.PrimaryLight,
    primaryVariant = AppColors.PrimaryVariantLight,
    secondary = AppColors.SecondaryLight,
    background = AppColors.BackgroundLight,

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
fun RobotsTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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