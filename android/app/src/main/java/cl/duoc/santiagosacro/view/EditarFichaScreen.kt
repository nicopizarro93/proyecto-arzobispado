package cl.duoc.santiagosacro.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.repository.HorarioRepository
import cl.duoc.santiagosacro.util.nombreDia
import cl.duoc.santiagosacro.view.componentes.CampoTexto
import cl.duoc.santiagosacro.viewmodel.MiRecintoViewModel

// El encargado edita la información de su recinto y su horario de atención.
// En esta entrega los campos vienen con los datos actuales y "Guardar cambios" vuelve atrás;
// en la prueba 3 los cambios se enviarán al backend.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarFichaScreen(
    onVolver: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar ficha") },
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
        val recinto = estado.recinto
        if (recinto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (estado.cargando) CircularProgressIndicator() else Text("Tu cuenta no tiene un recinto a cargo.")
            }
        } else {
            EditarFichaContenido(
                recinto = recinto,
                onGuardar = onVolver,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun EditarFichaContenido(
    recinto: RecintoConComuna,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Los campos parten con los datos actuales del recinto
    var nombre by rememberSaveable(recinto.id) { mutableStateOf(recinto.nombreOficial) }
    var calle by rememberSaveable(recinto.id) { mutableStateOf(recinto.calle) }
    var numero by rememberSaveable(recinto.id) { mutableStateOf(recinto.numeroCalle ?: "") }
    var historia by rememberSaveable(recinto.id) { mutableStateOf(recinto.descripcionHistorica ?: "") }

    // Horario: un texto y un interruptor (abierto o cerrado) por día de la semana
    val horarios = remember(recinto.id) { HorarioRepository.horariosDe(recinto.id) }
    val textos = remember(recinto.id) {
        (1..7).map { dia ->
            mutableStateOf(
                horarios.filter { it.diaSemana == dia }.joinToString(", ") { "${it.apertura}-${it.cierre}" }
            )
        }
    }
    val abiertos = remember(recinto.id) {
        (1..7).map { dia -> mutableStateOf(horarios.any { it.diaSemana == dia }) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(text = "Información del recinto", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoTexto(valor = nombre, onValorChange = { nombre = it }, etiqueta = "Nombre oficial")
            Row {
                CampoTexto(
                    valor = calle,
                    onValorChange = { calle = it },
                    etiqueta = "Calle",
                    modifier = Modifier.weight(2f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                CampoTexto(
                    valor = numero,
                    onValorChange = { numero = it },
                    etiqueta = "Número",
                    modifier = Modifier.weight(1f)
                )
            }
            CampoTexto(valor = historia, onValorChange = { historia = it }, etiqueta = "Historia", lineas = 5)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Horario de atención", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Escribe los bloques separados por coma, por ejemplo 09:00-13:00, 16:00-19:00.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..7).forEach { dia ->
                    val texto = textos[dia - 1]
                    val abierto = abiertos[dia - 1]
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = nombreDia(dia),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(84.dp)
                        )
                        OutlinedTextField(
                            value = if (abierto.value) texto.value else "Cerrado",
                            onValueChange = { texto.value = it },
                            enabled = abierto.value,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = abierto.value, onCheckedChange = { abierto.value = it })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Para una apertura o un cierre excepcional, publícalo como aviso desde \"Publicar evento o aviso\".",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text = "Guardar cambios", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
