package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.EstadoChip
import cl.duoc.aurasacrapp.view.componentes.HorarioSemana
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.FichaViewModel

// Planificar una visita a un recinto. Reutiliza FichaViewModel: la ruta "planificar/{recintoId}"
// trae el mismo argumento que la ficha. En la prueba 3 el formulario se enviará al backend.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificarVisitaScreen(
    onVolver: () -> Unit,
    viewModel: FichaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var fecha by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableStateOf("") }
    var personas by rememberSaveable { mutableStateOf(1) }
    var comentario by rememberSaveable { mutableStateOf("") }
    var curso by rememberSaveable { mutableStateOf("") }
    var solicitarGuia by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                // El docente planifica una salida pedagógica; el visitante, una visita
                title = { Text(if (estado.esDocente) "Salida pedagógica" else "Planificar visita") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Recinto que se visitará
            estado.recinto?.let { recinto ->
                Text(text = recinto.nombreOficial, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${recinto.tipoRecinto.etiqueta()} · ${recinto.comuna}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                EstadoChip(estado = recinto.estadoApertura)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Disponibilidad: horario del recinto y guías
            Text(text = "Disponibilidad", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            HorarioSemana(horarios = estado.horarios)
            if (estado.guias.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Guías disponibles",
                    style = MaterialTheme.typography.labelLarge
                )
                estado.guias.forEach { guia ->
                    Text(
                        text = "${guia.nombre} ${guia.apellido}: ${guia.disponibilidad.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Datos de la visita", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(
                    valor = fecha,
                    onValorChange = { fecha = it },
                    etiqueta = "Fecha (dd/mm/aaaa)",
                    teclado = KeyboardType.Number
                )
                CampoTexto(
                    valor = hora,
                    onValorChange = { hora = it },
                    etiqueta = "Hora de llegada (hh:mm)",
                    teclado = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Cantidad de personas con botones - y +
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Cantidad de personas",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                FilledTonalIconButton(onClick = { if (personas > 1) personas-- }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Menos personas")
                }
                Text(
                    text = personas.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                FilledTonalIconButton(onClick = { if (personas < 50) personas++ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Más personas")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Solo para docentes: curso y solicitud de guía
            if (estado.esDocente) {
                CampoTexto(
                    valor = curso,
                    onValorChange = { curso = it },
                    etiqueta = "Nivel o curso (ej. 3° básico B)"
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = solicitarGuia,
                        onCheckedChange = { solicitarGuia = it },
                        enabled = estado.guias.isNotEmpty()
                    )
                    Text(
                        text = if (estado.guias.isNotEmpty()) {
                            "Solicitar guía para el grupo"
                        } else {
                            "Este recinto no tiene guías disponibles"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            CampoTexto(
                valor = comentario,
                onValorChange = { comentario = it },
                etiqueta = "Comentario para el encargado (opcional)",
                lineas = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Revisa el estado del recinto antes de ir: puede cambiar por celebraciones o cierres especiales.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = if (estado.esDocente) "Solicitar visita" else "Agendar visita", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
