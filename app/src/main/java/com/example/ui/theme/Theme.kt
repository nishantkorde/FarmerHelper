package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = FarmGreenTertiary,
    onPrimary = Color.Black,
    primaryContainer = FarmGreenPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = HarvestGoldLight,
    onSecondary = Color.Black,
    tertiary = FarmGreenSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    outline = DarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = FarmGreenPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = FarmGreenPrimaryVariant,
    secondary = HarvestGold,
    onSecondary = Color.White,
    tertiary = FarmGreenSecondary,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    outline = LightOutline
  )

@Composable
fun FarmHelperTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  FarmHelperTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
