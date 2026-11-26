package cl.duoc.aurasacrapp.view

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
        opcionesGuia = {},
        informacion = {
            Seccion(titulo = estado.recinto?.nombreOficial ?: "Recinto") {
                HorarioSemana(horarios = estado.horarios)
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
    val datosFormulario by formulario.uiState.collectAsState()

    FormularioVisita(
        tituloVisitante = "Planificar visita a una ruta",
        tituloDocente = "Salida pedagógica a una ruta",
        onVolver = onVolver,
        onGuardar = onGuardar,
        formulario = formulario,
        opcionesGuia = {
            val paradas = estado.detalle?.ruta?.paradas.orEmpty()
            Text("¿En qué recintos necesitas un guía?")
            paradas.forEach { parada ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = parada.recintoId in datosFormulario.recintosGuia,
                        onCheckedChange = { formulario.alternarRecintoGuia(parada.recintoId) }
                    )
                    Text(
                        parada.nombreRecinto,
                        modifier = Modifier.clickable { formulario.alternarRecintoGuia(parada.recintoId) }
                    )
                }
            }
        },
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
    opcionesGuia: @Composable () -> Unit,
    informacion: @Composable () -> Unit
) {
    val datos by formulario.uiState.collectAsState()
    val esDocente = datos.esDocente
    val context = LocalContext.current

    // Cuando la visita queda agendada, se avisa una sola vez
    LaunchedEffect(datos.visitaAgendada) {
        if (datos.visitaAgendada) {
            // Si el guía no se pudo pedir, la visita igual quedó agendada: se avisa
            datos.avisoGuia?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
            onGuardar()
        }
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
        }

        // HU-23: visitantes y docentes pueden pedir un guía voluntario
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = datos.solicitarGuia, onCheckedChange = formulario::onSolicitarGuiaChange)
            Text(
                "Necesito un guía",
                modifier = Modifier.clickable { formulario.onSolicitarGuiaChange(!datos.solicitarGuia) }
            )
        }
        if (datos.solicitarGuia) opcionesGuia()
        datos.errores["guia"]?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

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
