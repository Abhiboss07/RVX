package com.rvx.companion.ui.theme

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

private val BrandDark = darkColorScheme(
    primary = Color(0xFF9DB4FF),
    secondary = Color(0xFF8DD3C7),
    background = Color(0xFF0B0B0F),
    surface = Color(0xFF14141B),
)
private val BrandLight = lightColorScheme(
    primary = Color(0xFF3A5BFF),
    secondary = Color(0xFF00786B),
)
private val AmoledDark = BrandDark.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceVariant = Color(0xFF0A0A0A),
)

/**
 * Material You: uses the wallpaper-derived dynamic palette on Android 12+ when enabled,
 * otherwise the brand palette. AMOLED forces true-black surfaces for OLED power savings.
 */
@Composable
fun RvxTheme(
    dynamicColor: Boolean,
    amoled: Boolean,
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> if (amoled) AmoledDark else BrandDark
        else -> BrandLight
    }.let { if (dark && amoled) it.copy(background = Color.Black, surface = Color.Black) else it }

    MaterialTheme(colorScheme = scheme, content = content)
}
