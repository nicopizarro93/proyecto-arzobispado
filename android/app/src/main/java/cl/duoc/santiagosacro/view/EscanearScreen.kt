package cl.duoc.santiagosacro.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.santiagosacro.view.componentes.PantallaBase

// PROVISORIA: el lector QR con ML Kit llega en la rama A4
@Composable
fun EscanearScreen(onRecintoEscaneado: (Long) -> Unit) {
    PantallaBase(
        titulo = "Escanear QR",
        descripcion = "Escanea el código QR que está en la entrada de cada recinto."
    ) {
        Button(onClick = { onRecintoEscaneado(2) }) { Text("Escanear") }
    }
}
