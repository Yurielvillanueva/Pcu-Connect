package edu.pcu.connect.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PcuLightColors = lightColorScheme(
    primary = PcuBlue,
    onPrimary = PcuWhite,
    primaryContainer = PcuBlueTintFaint,
    onPrimaryContainer = PcuBlueDark,
    secondary = PcuBlueLight,
    onSecondary = PcuWhite,
    secondaryContainer = PcuBlueTint,
    onSecondaryContainer = PcuInk,
    background = PcuWhite,
    onBackground = PcuInk,
    surface = PcuWhite,
    onSurface = PcuInk,
    surfaceVariant = PcuBlueTintFaint,
    onSurfaceVariant = PcuSlate,
    outline = PcuBlueTint,
    error = StatusEmergency,
)

private val PcuDarkColors = darkColorScheme(
    primary = PcuBlueLight,
    onPrimary = PcuInk,
    primaryContainer = PcuBlueDark,
    onPrimaryContainer = PcuBlueTint,
    secondary = PcuBlueTint,
    onSecondary = PcuInk,
    background = PcuInk,
    onBackground = PcuWhite,
    surface = Color2(0xFF171C36),
    onSurface = PcuWhite,
    error = StatusEmergency,
)

// Small helper so PcuDarkColors above reads cleanly without importing Color twice.
private fun Color2(value: Long) = androidx.compose.ui.graphics.Color(value)

@Composable
fun PCUConnectTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (useDarkTheme) PcuDarkColors else PcuLightColors
    MaterialTheme(
        colorScheme = colors,
        typography = PcuTypography,
        content = content,
    )
}
