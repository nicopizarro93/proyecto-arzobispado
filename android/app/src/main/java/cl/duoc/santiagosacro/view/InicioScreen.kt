package cl.duoc.santiagosacro.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.santiagosacro.view.componentes.PantallaBase

// PROVISORIA: la lista de recintos llega en la rama A3
@Composable
fun InicioScreen(onAbrirRecinto: (Long) -> Unit) {
    PantallaBase(
        titulo = "Explorar",
        descripcion = "Templos, museos, colegios y archivos de la red patrimonial de Santiago."
    ) {
        Button(onClick = { onAbrirRecinto(1) }) { Text("Capilla del Buen Pastor") }
    }
}
