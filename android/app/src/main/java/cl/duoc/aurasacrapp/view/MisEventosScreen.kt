package cl.duoc.aurasacrapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.EstadoEvento
import cl.duoc.aurasacrapp.util.formatearRango
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.MisEventosViewModel

// Eventos, avisos, aperturas y cierres del recinto del encargado: publicar, editar y cancelar
@Composable
fun MisEventosScreen(
    onVolver: () -> Unit,
    onPublicar: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: MisEventosViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Cada vez que se muestra la pantalla se piden los eventos (por ejemplo, al volver de publicar uno)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Eventos y avisos", onVolver = onVolver)

        Button(onClick = onPublicar, modifier = Modifier.fillMaxWidth()) {
            Text("Publicar evento o aviso")
        }

        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        when {
            estado.cargando -> Text("Cargando...")
            estado.eventos.isEmpty() && estado.error == null -> Text("Aún no has publicado eventos.")
            else -> estado.eventos.forEach { evento ->
                val cancelado = evento.estado == EstadoEvento.CANCELADO
                Item(
                    titulo = evento.titulo,
                    detalle = "${evento.tipo.etiqueta()} · ${formatearRango(evento.inicio, evento.fin)}" +
                        if (cancelado) "\nCancelado" else ""
                )
                // Solo se puede editar o cancelar un evento que sigue publicado
                if (!cancelado) {
                    Row {
                        OutlinedButton(onClick = { onEditar(evento.id) }) { Text("Editar") }
                        OutlinedButton(
                            onClick = { viewModel.cancelar(evento.id) },
                            modifier = Modifier.padding(start = 8.dp)
                        ) { Text("Cancelar evento") }
                    }
                }
            }
        }
    }
}
