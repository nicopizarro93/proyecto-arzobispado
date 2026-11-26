package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.RutaResumen
import cl.duoc.aurasacrapp.util.formatearDuracion
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.RutasViewModel

// Rutas sugeridas por la red patrimonial, las rutas propias y el botón para crear una ruta
@Composable
fun RutasScreen(
    onVolver: () -> Unit,
    onAbrirRuta: (Long) -> Unit,
    onCrearRuta: () -> Unit,
    viewModel: RutasViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Al volver a esta pantalla (por ejemplo, después de guardar una ruta) se pide la lista de nuevo
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Rutas sugeridas", onVolver = onVolver)
        Button(onClick = onCrearRuta, modifier = Modifier.fillMaxWidth()) {
            Text("Crear mi ruta")
        }

        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        when {
            estado.cargando -> Text("Cargando...")
            else -> {
                if (estado.sugeridas.isEmpty() && estado.error == null) {
                    Text("No hay rutas sugeridas.")
                }
                estado.sugeridas.forEach { ruta -> ItemRuta(ruta, onAbrirRuta) }

                if (estado.mias.isNotEmpty()) {
                    Text(text = "Mis rutas", style = MaterialTheme.typography.titleMedium)
                    estado.mias.forEach { ruta -> ItemRuta(ruta, onAbrirRuta) }
                }
            }
        }
    }
}

@Composable
private fun ItemRuta(ruta: RutaResumen, onAbrirRuta: (Long) -> Unit) {
    Item(
        titulo = ruta.nombre,
        detalle = "${ruta.modoTraslado.etiqueta()} · ${ruta.cantidadParadas} paradas · " +
            formatearDuracion(ruta.minutosTotales),
        onClick = { onAbrirRuta(ruta.id) }
    )
}
