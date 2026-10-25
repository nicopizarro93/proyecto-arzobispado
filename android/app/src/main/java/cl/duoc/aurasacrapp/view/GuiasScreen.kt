package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.repository.GuiaRepository
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.MiRecintoViewModel

// El encargado ve sus guías y responde las solicitudes de colegios.
// Las respuestas se ven solo en pantalla (no se guardan).
@Composable
fun GuiasScreen(
    onVolver: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val recintoId = estado.recinto?.id
    val guias = if (recintoId != null) GuiaRepository.guiasDe(recintoId) else emptyList()
    val solicitudes = if (recintoId != null) GuiaRepository.solicitudesDe(recintoId) else emptyList()

    // Respuesta a cada solicitud (id de la solicitud -> texto)
    val respuestas = remember { mutableStateMapOf<Long, String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Guías y solicitudes", onVolver = onVolver)

        Seccion(titulo = "Guías del recinto") {
            if (guias.isEmpty()) Text("Este recinto todavía no tiene guías.")
            guias.forEach { guia ->
                Text("${guia.nombre} ${guia.apellido}: ${guia.disponibilidad.joinToString(", ")}")
            }
        }

        Text("Solicitudes de colegios")
        if (solicitudes.isEmpty()) Text("No hay solicitudes pendientes.")
        solicitudes.forEach { solicitud ->
            Item(
                titulo = solicitud.institucion,
                detalle = "${solicitud.curso} · ${solicitud.personas} personas · ${solicitud.fecha}"
            )
            val respuesta = respuestas[solicitud.id]
            if (respuesta == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { respuestas[solicitud.id] = "Rechazada" }) { Text("Rechazar") }
                    Button(onClick = { respuestas[solicitud.id] = "Guía asignado" }) { Text("Asignar guía") }
                }
            } else {
                Text("Respuesta: $respuesta")
            }
        }
    }
}
