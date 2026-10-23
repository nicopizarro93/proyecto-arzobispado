package cl.duoc.santiagosacro

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.santiagosacro.navigation.AppNavigation
import cl.duoc.santiagosacro.ui.theme.SantiagoSacroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app siempre es clara: íconos oscuros en la barra de estado y de navegación,
        // aunque el teléfono esté en modo oscuro
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            SantiagoSacroTheme {
                AppNavigation()
            }
        }
    }
}
