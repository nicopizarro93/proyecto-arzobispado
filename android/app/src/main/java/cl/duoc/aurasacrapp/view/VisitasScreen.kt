package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.EstadoSolicitud
import cl.duoc.aurasacrapp.model.EstadoVisita
import cl.duoc.aurasacrapp.util.formatearFecha
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.VisitasViewModel

// Mis visitas: las que planifiqué (backend) y los recintos que registré escaneando el QR (Room)
@Composable
fun VisitasScreen(
    onAbrirRecinto: (Long) -> Unit,
    viewModel: VisitasViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Cada vez que se muestra la pantalla se piden las visitas planificadas
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Mis visitas")

        estado.mensaje?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        // Las visitas planificadas existen solo para visitantes y docentes
        if (estado.puedePlanificar) {
            Text(text = "Visitas planificadas", style = MaterialTheme.typography.titleMedium)
            when {
                estado.cargando -> Text("Cargando...")
                estado.agendadas.isEmpty() -> Text(
                    "No has planificado visitas. Abre la ficha de un recinto y toca Planificar visita."
                )
                else -> estado.agendadas.forEach { visita ->
                    val curso = visita.nivelCurso?.let { " · $it" }.orEmpty()
                    Item(
                        titulo = visita.destino,
                        detalle = "${formatearFecha(visita.inicio)}\n" +
                            "${visita.cantidadPersonas} personas$curso · ${visita.estado.etiqueta()}" +
                            (visita.observaciones?.let { "\n$it" } ?: ""),
                        onClick = visita.recintoId?.let { id -> { onAbrirRecinto(id) } }
                    )
                    // Estado de los guías pedidos para esta visita (HU-24)
                estado.solicitudes.filter { it.visitaId == visita.id }.forEach { solicitud ->
                    Text(
                        text = "Guía en ${solicitud.recinto}: " + when (solicitud.estado) {
                            EstadoSolicitud.PENDIENTE -> "pendiente"
                            EstadoSolicitud.ASIGNADA -> "asignado (${solicitud.guia})"
                            EstadoSolicitud.RECHAZADA -> "rechazado: ${solicitud.motivoRechazo}"
                            EstadoSolicitud.CANCELADA -> "cancelado"
                        },
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                // Solo se puede cancelar una visita que todavía está agendada
                    if (visita.estado == EstadoVisita.AGENDADA) {
                        OutlinedButton(onClick = { viewModel.cancelar(visita.id) }) {
                            Text("Cancelar visita")
                        }
                    }
                }
            }

        }

        Text(
            text = "Recintos escaneados",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        if (estado.escaneadas.isEmpty()) {
            Text(
                "Aún no registras visitas. Escanea el código QR en la entrada de un recinto para registrar tu primera visita."
            )
        } else {
            estado.escaneadas.forEach { visita ->
                Item(
                    titulo = visita.nombreRecinto,
                    detalle = "${visita.tipoRecinto.etiqueta()} · ${visita.comuna}\n${formatearFecha(visita.fechaHora)}",
                    onClick = { onAbrirRecinto(visita.recintoId) }
                )
            }
        }
    }
}
