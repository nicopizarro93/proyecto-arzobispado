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
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.RutaDetalleViewModel

// Detalle de una ruta: tiempo estimado y las paradas en orden, con el traslado entre ellas
@Composable
fun RutaDetalleScreen(
    onVolver: () -> Unit,
    onAbrirRecinto: (Long) -> Unit,
    viewModel: RutaDetalleViewModel = viewModel()
) {
    val detalle = viewModel.detalle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (detalle == null) {
            Encabezado(titulo = "Ruta", onVolver = onVolver)
            Text("No encontramos esta ruta.")
            return@Column
        }
        val ruta = detalle.ruta

        Encabezado(titulo = ruta.nombre, onVolver = onVolver)
        Text(ruta.descripcion)

        // Resumen del tiempo estimado (misma fórmula que el backend)
        Seccion(titulo = ruta.modoTraslado.etiqueta()) {
            Text("Tiempo total: ${formatearDuracion(detalle.minutosTotales)}")
            Text("Visitas: ${formatearDuracion(detalle.minutosVisita)}")
            Text("Traslados: ${formatearDuracion(detalle.minutosTraslado)}")
        }

        // Paradas en orden; debajo de cada una, el traslado hasta la siguiente
        ruta.paradas.forEachIndexed { i, parada ->
            Item(
                titulo = "${i + 1}. ${parada.nombreRecinto}",
                detalle = "${parada.comuna} · visita de ${formatearDuracion(parada.tiempoVisitaMin)}",
                onClick = { onAbrirRecinto(parada.recintoId) }
            )
            detalle.traslados.getOrNull(i)?.let { minutos ->
                Text("↓ ${formatearDuracion(minutos)} de traslado")
            }
        }

        Button(
            onClick = { ruta.paradas.firstOrNull()?.let { onAbrirRecinto(it.recintoId) } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar ruta")
        }
    }
}
