package cl.duoc.santiagosacro.view

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.santiagosacro.view.componentes.PantallaBase

// PROVISORIA: los datos del usuario llegan en la rama A2
@Composable
fun PerfilScreen(onCerrarSesion: () -> Unit) {
    PantallaBase(
        titulo = "Mi perfil",
        descripcion = "Tu cuenta de Santiago Sacro."
    ) {
        OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}
