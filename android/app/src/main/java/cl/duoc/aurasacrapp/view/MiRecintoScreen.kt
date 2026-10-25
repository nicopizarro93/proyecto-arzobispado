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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.MiRecintoViewModel

// Pantalla del encargado: cambia el estado de apertura de su recinto y abre el resto de su panel
@Composable
fun MiRecintoScreen(
    onVolver: () -> Unit,
    onVerFicha: (Long) -> Unit,
    onPublicarEvento: () -> Unit,
    onEditarFicha: () -> Unit,
    onGuias: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val recinto = estado.recinto

    // Aviso corto cuando se guarda el cambio
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let { texto ->
            Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (recinto == null) {
            Encabezado(titulo = "Mi recinto", onVolver = onVolver)
            Text(if (estado.cargando) "Cargando..." else "Tu cuenta no tiene un recinto a cargo.")
            return@Column
        }

        Encabezado(titulo = recinto.nombreOficial, onVolver = onVolver)
        Text("Estado actual: ${recinto.estadoApertura.etiqueta()}")

        // Un RadioButton por estado; se guarda con el botón
        Seccion(titulo = "Cambiar estado de apertura") {
            EstadoApertura.entries.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = estado.estadoElegido == opcion,
                        onClick = { viewModel.elegirEstado(opcion) }
                    )
                    Text(opcion.etiqueta())
                }
            }
            Button(
                onClick = viewModel::guardar,
                // Solo se puede guardar si eligió un estado distinto al actual
                enabled = !estado.guardando && estado.estadoElegido != recinto.estadoApertura
            ) {
                Text("Guardar estado")
            }
        }

        // Panel del encargado: todo lo que administra de su recinto
        OutlinedButton(onClick = onEditarFicha, modifier = Modifier.fillMaxWidth()) {
            Text("Editar ficha")
        }
        OutlinedButton(onClick = onPublicarEvento, modifier = Modifier.fillMaxWidth()) {
            Text("Publicar evento o aviso")
        }
        OutlinedButton(onClick = onGuias, modifier = Modifier.fillMaxWidth()) {
            Text("Guías y solicitudes")
        }
        OutlinedButton(onClick = { onVerFicha(recinto.id) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver ficha pública")
        }
    }
}
