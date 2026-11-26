package cl.duoc.aurasacrapp.view

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.EstadoSolicitud
import cl.duoc.aurasacrapp.model.SolicitudRecinto
import cl.duoc.aurasacrapp.util.formatearFecha
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.GuiasViewModel

// El encargado administra los guías de su recinto (HU-32) y responde las solicitudes que le llegan (HU-33)
@Composable
fun GuiasScreen(
    onVolver: () -> Unit,
    onDisponibilidad: (Long) -> Unit,
    viewModel: GuiasViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Al volver de la disponibilidad de un guía se piden de nuevo las listas
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Guías y solicitudes", onVolver = onVolver)

        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        if (estado.cargando) {
            Text("Cargando...")
            return@Column
        }

        // ---- Guías del recinto ----
        Seccion(titulo = "Guías del recinto") {
            if (estado.guias.isEmpty()) Text("Este recinto todavía no tiene guías.")
            estado.guias.forEach { guia ->
                Text(
                    text = "${guia.nombre} ${guia.apellido}" + if (guia.activo) "" else " (desactivado)",
                    style = MaterialTheme.typography.titleSmall
                )
                Text("${guia.disponibilidad.size} bloques de disponibilidad")
                Row {
                    OutlinedButton(onClick = { onDisponibilidad(guia.id) }) { Text("Disponibilidad") }
                    OutlinedButton(
                        onClick = { viewModel.cambiarActivo(guia) },
                        enabled = !estado.guardando,
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text(if (guia.activo) "Desactivar" else "Activar") }
                }
            }
        }

        // ---- Guía nuevo ----
        Seccion(titulo = "Agregar un guía") {
            CampoTexto(
                valor = estado.nombre, onValorChange = viewModel::onNombreChange,
                etiqueta = "Nombre", error = estado.erroresNuevo["nombre"]
            )
            CampoTexto(
                valor = estado.apellido, onValorChange = viewModel::onApellidoChange,
                etiqueta = "Apellido", error = estado.erroresNuevo["apellido"]
            )
            Button(
                onClick = { viewModel.crearGuia() },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar guía") }
        }

        // ---- Solicitudes ----
        Text(
            text = "Solicitudes de guía",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (estado.solicitudes.isEmpty()) Text("No hay solicitudes.")
        estado.solicitudes.forEach { solicitud ->
            Solicitud(solicitud = solicitud, viewModel = viewModel)
        }
    }
}

@Composable
private fun Solicitud(solicitud: SolicitudRecinto, viewModel: GuiasViewModel) {
    val estado by viewModel.uiState.collectAsState()
    val quien = solicitud.institucion ?: solicitud.solicitante
    val curso = solicitud.curso?.let { "$it · " }.orEmpty()
    Item(
        titulo = quien,
        detalle = "$curso${solicitud.personas} personas · ${formatearFecha(solicitud.inicio)}\n" +
            when (solicitud.estado) {
                EstadoSolicitud.PENDIENTE -> "Pendiente"
                EstadoSolicitud.ASIGNADA -> "Asignada a ${solicitud.guia}"
                EstadoSolicitud.RECHAZADA -> "Rechazada: ${solicitud.motivoRechazo}"
                EstadoSolicitud.CANCELADA -> "Cancelada"
            }
    )
    if (solicitud.estado != EstadoSolicitud.PENDIENTE) return

    when (solicitud.id) {
        // Asignar: se sugieren los guías disponibles en ese horario
        estado.asignandoId -> {
            if (estado.sugeridos.isEmpty()) {
                Text("No hay guías disponibles en ese horario. Agrega su disponibilidad primero.")
            } else {
                estado.sugeridos.forEach { guia ->
                    Button(
                        onClick = { viewModel.asignar(solicitud.id, guia.id) },
                        enabled = !estado.guardando,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar a ${guia.nombre} ${guia.apellido}") }
                }
            }
            OutlinedButton(onClick = { viewModel.cancelarAccion() }) { Text("Cancelar") }
        }
        // Rechazar: hay que escribir el motivo
        estado.rechazandoId -> {
            CampoTexto(
                valor = estado.motivo, onValorChange = viewModel::onMotivoChange,
                etiqueta = "Motivo del rechazo", lineas = 2, error = estado.errorMotivo
            )
            Row {
                Button(onClick = { viewModel.confirmarRechazo() }, enabled = !estado.guardando) {
                    Text("Confirmar rechazo")
                }
                OutlinedButton(
                    onClick = { viewModel.cancelarAccion() },
                    modifier = Modifier.padding(start = 8.dp)
                ) { Text("Cancelar") }
            }
        }
        else -> Row {
            Button(onClick = { viewModel.abrirAsignar(solicitud.id) }) { Text("Asignar guía") }
            OutlinedButton(
                onClick = { viewModel.abrirRechazo(solicitud.id) },
                modifier = Modifier.padding(start = 8.dp)
            ) { Text("Rechazar") }
        }
    }
}
