package cl.duoc.santiagosacro.view

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.santiagosacro.view.componentes.PantallaBase

// PROVISORIA: la ficha real llega en la rama A3
@Composable
fun FichaScreen(recintoId: Long, onVolver: () -> Unit) {
    PantallaBase(
        titulo = "Ficha del recinto",
        descripcion = "Recinto N° $recintoId"
    ) {
        OutlinedButton(onClick = onVolver) { Text("Volver") }
    }
}
