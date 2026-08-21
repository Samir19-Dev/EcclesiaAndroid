package com.ecclesia.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Color.White,
    secondary = AzulClaro,
    onSecondary = Color.White,
    secondaryContainer = DoradoTenue,
    onSecondaryContainer = AzulOscuro,
    tertiary = Dorado,
    onTertiary = AzulOscuro,
    tertiaryContainer = DoradoTenue,
    onTertiaryContainer = AzulOscuro,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = FondoClaro,
    onSurfaceVariant = TextoSuave,
    outline = BordeTarjeta,
    outlineVariant = BordeTarjeta,
    error = RojoPeligro,
    onError = Color.White,
    errorContainer = ErrorLoginFondo,
    onErrorContainer = ErrorLogin
)

@Composable
fun EcclesiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
