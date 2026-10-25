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
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.EventoItem
import cl.duoc.aurasacrapp.viewmodel.AgendaViewModel

// Agenda cultural: los próximos eventos de todos los recintos
@Composable
fun AgendaScreen(
    onAbrirEvento: (Long) -> Unit,
    viewModel: AgendaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Agenda cultural")

        when {
            estado.cargando -> Text("Cargando...")
            estado.eventos.isEmpty() -> Text("No hay eventos próximos.")
            else -> estado.eventos.forEach { evento ->
                EventoItem(evento = evento, mostrarRecinto = true, onClick = { onAbrirEvento(evento.id) })
            }
        }
    }
}
