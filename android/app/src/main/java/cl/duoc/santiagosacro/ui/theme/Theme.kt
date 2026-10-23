package cl.duoc.santiagosacro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SantiagoSacroColors = lightColorScheme(
    primary = Ciruela,
    onPrimary = TextoSobrePrimario,
    primaryContainer = CiruelaClaro,
    onPrimaryContainer = CiruelaOscuro,

    secondary = Turquesa,
    onSecondary = Color.White,
    secondaryContainer = TurquesaClaro,
    onSecondaryContainer = Turquesa,

    tertiary = Verde,
    onTertiary = Color.White,
    tertiaryContainer = VerdeClaro,
    onTertiaryContainer = Verde,

    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,

    // Fondos que usan barras de navegación, tarjetas y diálogos
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = SuperficieVariante,
    surfaceContainerHighest = SuperficieVariante,

    outline = BordeFuerte,
    outlineVariant = Borde,

    error = RojoError,
    onError = Color.White
)

@Composable
fun SantiagoSacroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SantiagoSacroColors,
        typography = Typography,
        content = content
    )
}