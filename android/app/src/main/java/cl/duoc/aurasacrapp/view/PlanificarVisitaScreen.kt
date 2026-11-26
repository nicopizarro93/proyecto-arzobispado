package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.util.formatearDuracion
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.HorarioSemana
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.FichaViewModel
import cl.duoc.aurasacrapp.viewmodel.PlanificarVisitaViewModel
import cl.duoc.aurasacrapp.viewmodel.RutaDetalleViewModel

// Planificar una visita a un recinto. Usa dos ViewModel: FichaViewModel (datos del recinto, horario
// y guías, porque la ruta "planificar/{recintoId}" trae el mismo argumento que la ficha) y
// PlanificarVisitaViewModel (el formulario y el envío al backend).
@Composable
fun PlanificarVisitaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: FichaViewModel = viewModel(),
    formulario: PlanificarVisitaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    FormularioVisita(
        tituloVisitante = "Planificar visita",
        tituloDocente = "Salida pedagógica",
        onVolver = onVolver,
        onGuardar = onGuardar,
        formulario = formulario,
        puedeSolicitarGuia = estado.guias.isNotEmpty(),
        informacion = {
            Seccion(titulo = estado.recinto?.nombreOficial ?: "Recinto") {
                HorarioSemana(horarios = estado.horarios)
                estado.guias.forEach { guia ->
                    Text("Guía: ${guia.nombre} ${guia.apellido} (${guia.disponibilidad.joinToString(", ")})")
                }
            }
        }
    )
}

// Planificar una visita a una ruta (la ruta "planificar-ruta/{rutaId}" trae el id de la ruta).
// Usa RutaDetalleViewModel para mostrar los datos de la ruta.
@Composable
fun PlanificarRutaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: RutaDetalleViewModel = viewModel(),
    formulario: PlanificarVisitaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    FormularioVisita(
        tituloVisitante = "Planificar visita a una ruta",
        tituloDocente = "Salida pedagógica a una ruta",
        onVolver = onVolver,
        onGuardar = onGuardar,
        formulario = formulario,
        puedeSolicitarGuia = false,
        informacion = {
            val detalle = estado.detalle
            Seccion(titulo = detalle?.ruta?.nombre ?: "Ruta") {
                if (detalle != null) {
                    Text("${detalle.ruta.modoTraslado.etiqueta()} · ${detalle.ruta.paradas.size} paradas")
                    Text("Tiempo estimado: ${formatearDuracion(detalle.minutosTotales)}")
                } else if (estado.error != null) {
                    Text(text = estado.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

// Formulario común: fecha, hora, personas, curso (solo docentes) y observaciones
@Composable
private fun FormularioVisita(
    tituloVisitante: String,
    tituloDocente: String,
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    formulario: PlanificarVisitaViewModel,
    puedeSolicitarGuia: Boolean,
    informacion: @Composable () -> Unit
) {
    val datos by formulario.uiState.collectAsState()
    val esDocente = datos.esDocente
    var solicitarGuia by rememberSaveable { mutableStateOf(false) }

    // Cuando la visita queda agendada, se avisa una sola vez
    LaunchedEffect(datos.visitaAgendada) {
        if (datos.visitaAgendada) onGuardar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = if (esDocente) tituloDocente else tituloVisitante, onVolver = onVolver)

        informacion()

        CampoTexto(
            valor = datos.fecha, onValorChange = formulario::onFechaChange,
            etiqueta = "Fecha (dd/mm/aaaa)", teclado = KeyboardType.Number, error = datos.errores["fecha"]
        )
        CampoTexto(
            valor = datos.hora, onValorChange = formulario::onHoraChange,
            etiqueta = "Hora de llegada (hh:mm)", teclado = KeyboardType.Number, error = datos.errores["hora"]
        )

        // Cantidad de personas con botones - y +
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Personas: ${datos.personas}  ")
            OutlinedButton(onClick = { formulario.onPersonasChange(datos.personas - 1) }) { Text("-") }
            OutlinedButton(onClick = { formulario.onPersonasChange(datos.personas + 1) }) { Text("+") }
        }

        // Solo para docentes: curso y solicitud de guía
        if (esDocente) {
            CampoTexto(
                valor = datos.curso, onValorChange = formulario::onCursoChange,
                etiqueta = "Nivel o curso (ej. 3° básico B)", error = datos.errores["curso"]
            )
            if (puedeSolicitarGuia) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = solicitarGuia, onCheckedChange = { solicitarGuia = it })
                    Text("Solicitar guía para el grupo", modifier = Modifier.clickable { solicitarGuia = !solicitarGuia })
                }
            }
        }

        CampoTexto(
            valor = datos.observaciones, onValorChange = formulario::onObservacionesChange,
            etiqueta = "Observaciones (opcional)", lineas = 3, error = datos.errores["observaciones"]
        )

        // Error del servidor (por ejemplo, el recinto ya no está disponible)
        datos.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { formulario.agendar() },
            enabled = !datos.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    datos.guardando -> "Enviando..."
                    esDocente -> "Solicitar visita"
                    else -> "Agendar visita"
                }
            )
        }
    }
}
