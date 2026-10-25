package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.EventoItem
import cl.duoc.aurasacrapp.view.componentes.HorarioSemana
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.direccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.FichaViewModel

// Ficha de un recinto: datos, horario, historia, eventos y el botón para planificar una visita
@Composable
fun FichaScreen(
    onVolver: () -> Unit,
    onAbrirEvento: (Long) -> Unit,
    onPlanificarVisita: (Long) -> Unit,
    viewModel: FichaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val recinto = estado.recinto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (recinto == null) {
            Encabezado(titulo = "Ficha del recinto", onVolver = onVolver)
            Text(if (estado.cargando) "Cargando..." else "No encontramos este recinto.")
            return@Column
        }

        Encabezado(titulo = recinto.nombreOficial, onVolver = onVolver)

        Seccion(titulo = "Datos") {
            Text("Tipo: ${recinto.tipoRecinto.etiqueta()}")
            Text("Dirección: ${recinto.direccion()}, ${recinto.comuna}")
            Text("Estado: ${recinto.estadoApertura.etiqueta()}")
        }

        Seccion(titulo = "Horario") {
            HorarioSemana(horarios = estado.horarios)
        }

        Seccion(titulo = "Historia") {
            Text(recinto.descripcionHistorica ?: "Este recinto aún no tiene reseña histórica.")
        }

        Button(onClick = { onPlanificarVisita(recinto.id) }, modifier = Modifier.fillMaxWidth()) {
            Text("Planificar visita")
        }

        // Próximos eventos del recinto (solo si tiene)
        if (estado.eventos.isNotEmpty()) {
            Text(text = "Próximos eventos", modifier = Modifier.padding(top = 12.dp))
            estado.eventos.forEach { evento ->
                EventoItem(evento = evento, mostrarRecinto = false, onClick = { onAbrirEvento(evento.id) })
            }
        }

        Text(
            text = "¿Estás aquí? Escanea el código QR de la entrada para registrar tu visita.",
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
