package cl.duoc.aurasacrapp.view

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.util.nombreDia
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.EditarFichaViewModel

// El encargado edita la ficha de su recinto: la reseña histórica y los bloques de horario.
// El nombre y la dirección los administra el administrador de la red.
@Composable
fun EditarFichaScreen(
    onVolver: () -> Unit,
    viewModel: EditarFichaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Aviso corto de lo que se guardó
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
        Encabezado(titulo = "Editar ficha", onVolver = onVolver)

        if (estado.cargando) {
            Text("Cargando...")
            return@Column
        }
        if (estado.recintoId == null) {
            Text(
                text = estado.error ?: "Tu cuenta no tiene un recinto a cargo.",
                color = MaterialTheme.colorScheme.error
            )
            return@Column
        }

        // ---- Reseña histórica ----
        Seccion(titulo = "Reseña histórica") {
            CampoTexto(
                valor = estado.historia, onValorChange = viewModel::onHistoriaChange,
                etiqueta = "Historia del recinto", lineas = 5, error = estado.errores["historia"]
            )
            Button(
                onClick = { viewModel.guardarHistoria() },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar reseña")
            }
        }

        // ---- Horario regular: un bloque por línea ----
        Seccion(titulo = "Horario de atención") {
            if (estado.horarios.isEmpty()) Text("Aún no hay horarios.")
            estado.horarios.sortedWith(compareBy({ it.diaSemana }, { it.apertura })).forEach { horario ->
                Text("${nombreDia(horario.diaSemana)}: ${horario.apertura}–${horario.cierre}")
                Row {
                    OutlinedButton(onClick = { viewModel.editarHorario(horario) }) { Text("Editar") }
                    OutlinedButton(
                        onClick = { viewModel.eliminarHorario(horario.id) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text("Eliminar") }
                }
            }
        }

        // ---- Formulario del bloque (nuevo o en edición) ----
        Seccion(titulo = if (estado.editandoHorarioId == null) "Agregar un bloque de horario" else "Editar bloque de horario") {
            // Un chip por día de la semana (1 = lunes ... 7 = domingo)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                (1..7).forEach { dia ->
                    FilterChip(
                        selected = estado.dia == dia,
                        onClick = { viewModel.onDiaChange(dia) },
                        label = { Text(nombreDia(dia).take(3)) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }
            CampoTexto(
                valor = estado.apertura, onValorChange = viewModel::onAperturaChange,
                etiqueta = "Apertura (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["apertura"]
            )
            CampoTexto(
                valor = estado.cierre, onValorChange = viewModel::onCierreChange,
                etiqueta = "Cierre (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["cierre"]
            )
            Row {
                Button(onClick = { viewModel.guardarHorario() }, enabled = !estado.guardando) {
                    Text(if (estado.editandoHorarioId == null) "Agregar bloque" else "Guardar bloque")
                }
                if (estado.editandoHorarioId != null) {
                    OutlinedButton(
                        onClick = { viewModel.cancelarEdicionHorario() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text("Cancelar") }
                }
            }
        }

        // Error del servidor
        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
    }
}
