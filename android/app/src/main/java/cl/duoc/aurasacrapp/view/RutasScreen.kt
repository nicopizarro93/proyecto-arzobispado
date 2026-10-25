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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.util.formatearDuracion
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.RutasViewModel

// Rutas sugeridas por la red patrimonial y el botón para crear una ruta propia
@Composable
fun RutasScreen(
    onVolver: () -> Unit,
    onAbrirRuta: (Long) -> Unit,
    onCrearRuta: () -> Unit,
    viewModel: RutasViewModel = viewModel()
) {
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

        viewModel.rutas.forEach { resumen ->
            val ruta = resumen.ruta
            Item(
                titulo = ruta.nombre,
                detalle = "${ruta.modoTraslado.etiqueta()} · ${ruta.paradas.size} paradas · " +
                    formatearDuracion(resumen.minutosTotales),
                onClick = { onAbrirRuta(ruta.id) }
            )
        }
    }
}
