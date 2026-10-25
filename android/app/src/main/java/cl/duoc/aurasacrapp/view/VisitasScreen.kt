package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.util.formatearFecha
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.VisitasViewModel

// Historial de visitas del usuario (las que registró escaneando el QR)
@Composable
fun VisitasScreen(
    onAbrirRecinto: (Long) -> Unit,
    viewModel: VisitasViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Mis visitas")

        when {
            estado.cargando -> Text("Cargando...")
            estado.visitas.isEmpty() -> Text(
                "Aún no registras visitas. Escanea el código QR en la entrada de un recinto para registrar tu primera visita."
            )
            else -> estado.visitas.forEach { visita ->
                Item(
                    titulo = visita.nombreRecinto,
                    detalle = "${visita.tipoRecinto.etiqueta()} · ${visita.comuna}\n${formatearFecha(visita.fechaHora)}",
                    onClick = { onAbrirRecinto(visita.recintoId) }
                )
            }
        }
    }
}
