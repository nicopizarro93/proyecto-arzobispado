package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.TipoRecinto
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.view.componentes.etiquetaPlural
import cl.duoc.aurasacrapp.viewmodel.InicioViewModel

// Pantalla principal: buscador, filtros combinables (tipo, comuna, estado y favoritos) y la lista de recintos
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
    val filtros = estado.filtros
    var menuComunaAbierto by remember { mutableStateOf(false) }

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

        // Aviso si no se pudo actualizar desde el backend
        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

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
                selected = filtros.tipo == null,
                onClick = { viewModel.onTipoSeleccionado(null) },
                label = { Text("Todos") }
            )
            TipoRecinto.entries.forEach { tipo ->
                FilterChip(
                    selected = filtros.tipo == tipo,
                    onClick = { viewModel.onTipoSeleccionado(tipo) },
                    label = { Text(tipo.etiquetaPlural()) }
                )
            }
        }

        // Más filtros, combinables con los anteriores: favoritos, comuna y estado
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtros.soloFavoritos,
                onClick = { viewModel.onSoloFavoritos(!filtros.soloFavoritos) },
                label = { Text("★ Favoritos") }
            )
            // Comuna: un menú con las comunas que tienen recintos
            Box {
                FilterChip(
                    selected = filtros.comuna != null,
                    onClick = { menuComunaAbierto = true },
                    label = { Text(filtros.comuna ?: "Comuna ▾") }
                )
                DropdownMenu(expanded = menuComunaAbierto, onDismissRequest = { menuComunaAbierto = false }) {
                    DropdownMenuItem(
                        text = { Text("Todas las comunas") },
                        onClick = {
                            viewModel.onComunaSeleccionada(null)
                            menuComunaAbierto = false
                        }
                    )
                    estado.comunas.forEach { comuna ->
                        DropdownMenuItem(
                            text = { Text(comuna) },
                            onClick = {
                                viewModel.onComunaSeleccionada(comuna)
                                menuComunaAbierto = false
                            }
                        )
                    }
                }
            }
            EstadoApertura.entries.forEach { opcion ->
                FilterChip(
                    selected = filtros.estado == opcion,
                    onClick = { viewModel.onEstadoSeleccionado(opcion) },
                    label = { Text(opcion.etiqueta()) }
                )
            }
        }

        // Lista de recintos (o un mensaje si no hay resultados)
        when {
            estado.cargando -> Text("Cargando...")
            estado.recintos.isEmpty() -> {
                // Con solo "Favoritos" activo, lo más probable es que aún no haya ninguno
                val soloFavoritosVacio = filtros.soloFavoritos && filtros.tipo == null &&
                    filtros.comuna == null && filtros.estado == null && busqueda.isBlank()
                Text(
                    text = if (soloFavoritosVacio) {
                        "Aún no tienes recintos favoritos. Abre la ficha de un recinto y márcalo como favorito."
                    } else {
                        "No hay recintos que coincidan con tu búsqueda."
                    },
                    modifier = Modifier.padding(top = 8.dp)
                )
                // Si hay filtros o búsqueda, se ofrece dejar la lista completa
                if (estado.hayFiltros) {
                    OutlinedButton(onClick = { viewModel.limpiarFiltros() }) { Text("Limpiar filtros") }
                }
            }
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
