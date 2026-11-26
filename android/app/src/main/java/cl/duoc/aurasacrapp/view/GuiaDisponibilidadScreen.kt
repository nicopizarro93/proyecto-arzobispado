package cl.duoc.aurasacrapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.util.formatearFecha
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.GuiaDisponibilidadViewModel

// Bloques de tiempo en que un guía está disponible: se agregan con fecha y hora de inicio y de término
@Composable
fun GuiaDisponibilidadScreen(
    onVolver: () -> Unit,
    viewModel: GuiaDisponibilidadViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val guia = estado.guia

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
        Encabezado(titulo = guia?.let { "${it.nombre} ${it.apellido}" } ?: "Disponibilidad", onVolver = onVolver)

        if (guia == null) {
            when {
                estado.cargando -> Text("Cargando...")
                else -> Text(
                    text = estado.error ?: "No encontramos este guía.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            return@Column
        }

        Seccion(titulo = "Bloques de disponibilidad") {
            if (guia.disponibilidad.isEmpty()) Text("Este guía aún no tiene disponibilidad.")
            guia.disponibilidad.forEach { bloque ->
                Item(
                    titulo = "Desde ${formatearFecha(bloque.inicio)}",
                    detalle = "Hasta ${formatearFecha(bloque.fin)}"
                )
                OutlinedButton(onClick = { viewModel.eliminar(bloque.id) }, enabled = !estado.guardando) {
                    Text("Eliminar")
                }
            }
        }

        Seccion(titulo = "Agregar un bloque") {
            CampoTexto(
                valor = estado.fechaInicio, onValorChange = viewModel::onFechaInicioChange,
                etiqueta = "Inicio: fecha (dd/mm/aaaa)", teclado = KeyboardType.Number
            )
            CampoTexto(
                valor = estado.horaInicio, onValorChange = viewModel::onHoraInicioChange,
                etiqueta = "Inicio: hora (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["inicio"]
            )
            CampoTexto(
                valor = estado.fechaFin, onValorChange = viewModel::onFechaFinChange,
                etiqueta = "Término: fecha (dd/mm/aaaa)", teclado = KeyboardType.Number
            )
            CampoTexto(
                valor = estado.horaFin, onValorChange = viewModel::onHoraFinChange,
                etiqueta = "Término: hora (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["fin"]
            )
            estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = { viewModel.agregar() },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (estado.guardando) "Guardando..." else "Agregar bloque") }
        }
    }
}
