package cl.duoc.santiagosacro.view

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.model.TipoEvento
import cl.duoc.santiagosacro.view.componentes.CampoTexto
import cl.duoc.santiagosacro.view.componentes.etiqueta
import cl.duoc.santiagosacro.viewmodel.MiRecintoViewModel

// El encargado publica un evento o aviso de su recinto. En esta entrega el formulario
// vuelve a "Mi recinto"; en la prueba 3 se enviará al backend (endpoint del encargado).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarEventoScreen(
    onVolver: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var tipo by rememberSaveable { mutableStateOf(TipoEvento.CONCIERTO) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }
    var horaInicio by rememberSaveable { mutableStateOf("") }
    var horaTermino by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar evento") },
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
            estado.recinto?.let { recinto ->
                Text(
                    text = "Se publicará en",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = recinto.nombreOficial, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tipo de evento
            Text(text = "Tipo", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TipoEvento.entries.forEach { opcion ->
                    FilterChip(
                        selected = tipo == opcion,
                        onClick = { tipo = opcion },
                        label = { Text(opcion.etiqueta()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(valor = titulo, onValorChange = { titulo = it }, etiqueta = "Título")
                CampoTexto(
                    valor = descripcion,
                    onValorChange = { descripcion = it },
                    etiqueta = "Descripción",
                    lineas = 3
                )
                CampoTexto(
                    valor = fecha,
                    onValorChange = { fecha = it },
                    etiqueta = "Fecha (dd/mm/aaaa)",
                    teclado = KeyboardType.Number
                )
                CampoTexto(
                    valor = horaInicio,
                    onValorChange = { horaInicio = it },
                    etiqueta = "Hora de inicio (hh:mm)",
                    teclado = KeyboardType.Number
                )
                CampoTexto(
                    valor = horaTermino,
                    onValorChange = { horaTermino = it },
                    etiqueta = "Hora de término (opcional)",
                    teclado = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "El evento aparecerá en la Agenda cultural y en la ficha del recinto.",
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
                Text(text = "Publicar", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
