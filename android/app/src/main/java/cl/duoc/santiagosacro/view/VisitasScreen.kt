package cl.duoc.santiagosacro.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.santiagosacro.view.componentes.PantallaBase

// PROVISORIA: el historial de visitas llega en la rama A4
@Composable
fun VisitasScreen(onAbrirRecinto: (Long) -> Unit) {
    PantallaBase(
        titulo = "Mis visitas",
        descripcion = "Los lugares que has visitado."
    ) {
        Button(onClick = { onAbrirRecinto(3) }) { Text("Ver última visita") }
    }
}
