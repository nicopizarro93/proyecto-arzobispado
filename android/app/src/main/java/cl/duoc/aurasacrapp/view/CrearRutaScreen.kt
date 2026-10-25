package cl.duoc.aurasacrapp.view

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.MapaViewModel

// Crear una ruta propia: nombre, modo de traslado y recintos a visitar.
// El formulario es solo visual: "Guardar ruta" muestra un aviso y vuelve a Rutas.
// La lista de recintos se toma de MapaViewModel (la misma que usa el mapa).
@Composable
fun CrearRutaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: MapaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var nombre by rememberSaveable { mutableStateOf("") }
    var modo by rememberSaveable { mutableStateOf(ModoTraslado.A_PIE) }
    val seleccionados = remember { mutableStateListOf<Long>() }  // ids de los recintos marcados

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Crear ruta", onVolver = onVolver)
        CampoTexto(valor = nombre, onValorChange = { nombre = it }, etiqueta = "Nombre de la ruta")

        // Un RadioButton por cada modo de traslado: solo se puede elegir uno
        Seccion(titulo = "¿Cómo te mueves?") {
            ModoTraslado.entries.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = modo == opcion, onClick = { modo = opcion })
                    Text(text = opcion.etiqueta())
                }
            }
        }

        // Un Checkbox por cada recinto: se pueden marcar varios
        Seccion(titulo = "Recintos a visitar") {
            estado.recintos.forEach { recinto ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = recinto.id in seleccionados,
                        onCheckedChange = { marcado ->
                            if (marcado) seleccionados.add(recinto.id) else seleccionados.remove(recinto.id)
                        }
                    )
                    Text(text = "${recinto.nombreOficial} (${recinto.comuna})")
                }
            }
        }

        Text(
            text = "Paradas elegidas: ${seleccionados.size}",
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onGuardar,
            enabled = seleccionados.size >= 2,  // una ruta necesita al menos 2 recintos
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar ruta")
        }
    }
}

