package cl.duoc.aurasacrapp

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.aurasacrapp.navigation.AppNavigation
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.ui.theme.AuraSacrappTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Android 17 exige este permiso para conectarse a la red local (el backend del PC, 10.0.2.2)
        if (checkSelfPermission(PERMISO_RED_LOCAL) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(PERMISO_RED_LOCAL), 1)
        }
        // Recupera la sesión guardada (si el usuario ya había iniciado sesión)
        SesionRepository.inicializar(this)
        // La app siempre es clara: íconos oscuros en la barra de estado y de navegación,
        // aunque el teléfono esté en modo oscuro
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            AuraSacrappTheme {
                AppNavigation()
            }
        }
    }

    private companion object {
        const val PERMISO_RED_LOCAL = "android.permission.ACCESS_LOCAL_NETWORK"
    }
}
