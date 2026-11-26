package cl.duoc.aurasacrapp.view

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.EditarVisitaViewModel

// Cambiar la fecha, la hora y la cantidad de personas de una visita que todavía no se realiza
@Composable
fun EditarVisitaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: EditarVisitaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Cuando la visita queda actualizada, se avisa una sola vez
    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onGuardar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Editar visita", onVolver = onVolver)

        if (estado.cargando) {
            Text("Cargando...")
            return@Column
        }
        if (estado.destino.isEmpty()) {
            Text(text = estado.error ?: "No encontramos esta visita.", color = MaterialTheme.colorScheme.error)
            return@Column
        }

        Seccion(titulo = estado.destino) {
            Text("Solo puedes cambiar la fecha, la hora y la cantidad de personas.")
        }

        CampoTexto(
            valor = estado.fecha, onValorChange = viewModel::onFechaChange,
            etiqueta = "Fecha (dd/mm/aaaa)", teclado = KeyboardType.Number, error = estado.errores["fecha"]
        )
        CampoTexto(
            valor = estado.hora, onValorChange = viewModel::onHoraChange,
            etiqueta = "Hora de llegada (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["hora"]
        )

        // Cantidad de personas con botones - y +
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Personas: ${estado.personas}  ")
            OutlinedButton(onClick = { viewModel.onPersonasChange(estado.personas - 1) }) { Text("-") }
            OutlinedButton(onClick = { viewModel.onPersonasChange(estado.personas + 1) }) { Text("+") }
        }

        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { viewModel.guardar() },
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.guardando) "Guardando..." else "Guardar cambios")
        }
    }
}
