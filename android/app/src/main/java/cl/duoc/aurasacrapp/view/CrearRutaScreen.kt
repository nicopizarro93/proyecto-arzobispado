package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.CrearRutaViewModel

// Crear una ruta propia: nombre, modo de traslado y recintos a visitar.
// "Guardar ruta" la envía al backend, que calcula su tiempo estimado.
@Composable
fun CrearRutaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: CrearRutaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Cuando la ruta queda guardada, se avisa una sola vez
    LaunchedEffect(estado.rutaGuardada) {
        if (estado.rutaGuardada) onGuardar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Crear ruta", onVolver = onVolver)

        CampoTexto(
            valor = estado.nombre,
            onValorChange = viewModel::onNombreChange,
            etiqueta = "Nombre de la ruta"
        )

        // Un RadioButton por cada modo de traslado: solo se puede elegir uno
        Seccion(titulo = "¿Cómo te mueves?") {
            ModoTraslado.entries.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = estado.modo == opcion, onClick = { viewModel.onModoChange(opcion) })
                    Text(text = opcion.etiqueta(), modifier = Modifier.clickable { viewModel.onModoChange(opcion) })
                }
            }
        }

        // Un Checkbox por cada recinto: se pueden marcar varios; el orden de marcado es el de las paradas
        Seccion(titulo = "Recintos a visitar") {
            estado.recintos.forEach { recinto ->
                val posicion = estado.seleccionados.indexOf(recinto.id)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = posicion >= 0,
                        onCheckedChange = { viewModel.alternarRecinto(recinto.id) }
                    )
                    val orden = if (posicion >= 0) "${posicion + 1}. " else ""
                    Text(
                        text = "$orden${recinto.nombreOficial} (${recinto.comuna})",
                        modifier = Modifier.clickable { viewModel.alternarRecinto(recinto.id) }
                    )
                }
            }
        }

        Text(
            text = "Paradas elegidas: ${estado.seleccionados.size}",
            color = MaterialTheme.colorScheme.secondary
        )
        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.guardar() },
            enabled = estado.puedeGuardar,   // nombre y al menos 2 recintos
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.guardando) "Guardando..." else "Guardar ruta")
        }
    }
}
