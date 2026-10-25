package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.EstadoEvento
import cl.duoc.aurasacrapp.util.formatearRango
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.EventoViewModel

// Detalle de un evento: tipo, cuándo, dónde, descripción y acceso a la ficha del recinto
@Composable
fun EventoScreen(
    onVolver: () -> Unit,
    onAbrirRecinto: (Long) -> Unit,
    viewModel: EventoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val evento = estado.evento

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (evento == null) {
            Encabezado(titulo = "Evento", onVolver = onVolver)
            Text(if (estado.cargando) "Cargando..." else "No encontramos este evento.")
            return@Column
        }

        Encabezado(titulo = evento.titulo, onVolver = onVolver)
        if (evento.estado == EstadoEvento.CANCELADO) {
            Text(text = "Este evento fue cancelado", color = MaterialTheme.colorScheme.error)
        }

        Seccion(titulo = evento.tipoEvento.etiqueta()) {
            Text("Cuándo: ${formatearRango(evento.fechaHoraInicio, evento.fechaHoraFin)}")
            Text("Dónde: ${evento.nombreRecinto} · ${evento.comuna}")
        }

        // Descripción (si tiene)
        evento.descripcion?.let { Text(it) }

        OutlinedButton(onClick = { onAbrirRecinto(evento.recintoId) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver ficha del recinto")
        }
    }
}
