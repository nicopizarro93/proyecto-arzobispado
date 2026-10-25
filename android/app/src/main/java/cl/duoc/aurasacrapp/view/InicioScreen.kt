package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.TipoRecinto
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.view.componentes.etiquetaPlural
import cl.duoc.aurasacrapp.viewmodel.InicioViewModel

// Pantalla principal: buscador, filtro por tipo y la lista de recintos
@Composable
fun InicioScreen(
    onAbrirRecinto: (Long) -> Unit,
    onAdministrarRecinto: () -> Unit,
    onVerRutas: () -> Unit,
    onVerMapa: () -> Unit,
    viewModel: InicioViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (estado.nombreUsuario.isNotEmpty()) {
            Text(text = "Hola, ${estado.nombreUsuario}")
        }
        Encabezado(titulo = "Explorar")

        // Accesos al mapa y a las rutas
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onVerMapa) { Text("Ver mapa") }
            OutlinedButton(onClick = onVerRutas) { Text("Rutas") }
        }

        // Solo para encargados: acceso a "Mi recinto"
        estado.recintoACargo?.let { recinto ->
            Button(onClick = onAdministrarRecinto, modifier = Modifier.fillMaxWidth()) {
                Text("Administrar ${recinto.nombreOficial}")
            }
        }

        CampoTexto(valor = busqueda, onValorChange = viewModel::onBusquedaChange, etiqueta = "Buscar por nombre o comuna")

        // Filtros por tipo de recinto (null = todos)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = estado.tipoSeleccionado == null,
                onClick = { viewModel.onTipoSeleccionado(null) },
                label = { Text("Todos") }
            )
            TipoRecinto.entries.forEach { tipo ->
                FilterChip(
                    selected = estado.tipoSeleccionado == tipo,
                    onClick = { viewModel.onTipoSeleccionado(tipo) },
                    label = { Text(tipo.etiquetaPlural()) }
                )
            }
        }

        // Lista de recintos (o un mensaje si no hay resultados)
        when {
            estado.cargando -> Text("Cargando...")
            estado.recintos.isEmpty() -> Text("No hay recintos que coincidan con tu búsqueda.")
            else -> estado.recintos.forEach { recinto ->
                Item(
                    titulo = recinto.nombreOficial,
                    detalle = "${recinto.tipoRecinto.etiqueta()} · ${recinto.comuna} · ${recinto.estadoApertura.etiqueta()}",
                    onClick = { onAbrirRecinto(recinto.id) }
                )
            }
        }
    }
}
