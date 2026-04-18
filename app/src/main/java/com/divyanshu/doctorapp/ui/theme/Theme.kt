package com.divyanshu.doctorapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = YellowText,
    primaryContainer = DarkGreen,
    onPrimaryContainer = YellowText,
    secondary = DarkGreen,
    onSecondary = YellowText,
    background = PureBlack,
    surface = PureBlack,
    onBackground = YellowText,
    onSurface = YellowText,
    onSurfaceVariant = YellowText,
    outline = YellowText,
    error = Red80
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = PureWhite,
    primaryContainer = Green80,
    onPrimaryContainer = PureWhite,
    secondary = Teal40,
    onSecondary = PureWhite,
    background = PureWhite,
    surface = PureWhite,
    onBackground = PureBlack,
    onSurface = PureBlack,
    onSurfaceVariant = Grey20,
    outline = Grey20,
    error = Red40
)

@Composable
fun DoctorAppointmentAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}