package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.HorarioSemana
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.FichaViewModel

// Planificar una visita a un recinto. Reutiliza FichaViewModel: la ruta "planificar/{recintoId}"
// trae el mismo argumento que la ficha. El formulario es solo visual: el botón muestra un aviso y vuelve.
@Composable
fun PlanificarVisitaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: FichaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var fecha by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableStateOf("") }
    var personas by rememberSaveable { mutableIntStateOf(1) }
    var curso by rememberSaveable { mutableStateOf("") }
    var solicitarGuia by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // El docente planifica una salida pedagógica; el visitante, una visita
        Encabezado(
            titulo = if (estado.esDocente) "Salida pedagógica" else "Planificar visita",
            onVolver = onVolver
        )

        Seccion(titulo = estado.recinto?.nombreOficial ?: "Recinto") {
            HorarioSemana(horarios = estado.horarios)
            estado.guias.forEach { guia ->
                Text("Guía: ${guia.nombre} ${guia.apellido} (${guia.disponibilidad.joinToString(", ")})")
            }
        }

        CampoTexto(valor = fecha, onValorChange = { fecha = it }, etiqueta = "Fecha (dd/mm/aaaa)", teclado = KeyboardType.Number)
        CampoTexto(valor = hora, onValorChange = { hora = it }, etiqueta = "Hora de llegada (hh:mm)", teclado = KeyboardType.Number)

        // Cantidad de personas con botones - y +
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Personas: $personas  ")
            OutlinedButton(onClick = { if (personas > 1) personas-- }) { Text("-") }
            OutlinedButton(onClick = { if (personas < 50) personas++ }) { Text("+") }
        }

        // Solo para docentes: curso y solicitud de guía
        if (estado.esDocente) {
            CampoTexto(valor = curso, onValorChange = { curso = it }, etiqueta = "Nivel o curso (ej. 3° básico B)")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = solicitarGuia,
                    onCheckedChange = { solicitarGuia = it },
                    enabled = estado.guias.isNotEmpty()
                )
                Text("Solicitar guía para el grupo")
            }
        }

        Button(onClick = onGuardar, modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.esDocente) "Solicitar visita" else "Agendar visita")
        }
    }
}
