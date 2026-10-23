package cl.duoc.santiagosacro.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.view.componentes.EventoItem
import cl.duoc.santiagosacro.viewmodel.AgendaViewModel

@Composable
fun AgendaScreen(
    onAbrirEvento: (Long) -> Unit,
    viewModel: AgendaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(text = "Agenda cultural", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Conciertos, exposiciones y celebraciones en los recintos de la red.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        when {
            estado.cargando -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            estado.eventos.isEmpty() -> item {
                Text(
                    text = "No hay eventos próximos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
            else -> items(estado.eventos, key = { it.id }) { evento ->
                EventoItem(
                    evento = evento,
                    mostrarRecinto = true,
                    onClick = { onAbrirEvento(evento.id) }
                )
            }
        }
    }
}
