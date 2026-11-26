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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.TipoEvento
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.EventoFormularioViewModel

// El encargado publica un evento, aviso, apertura o cierre de su recinto, o edita uno ya publicado.
// El mismo formulario sirve para las dos cosas (al editar, la ruta trae el id del evento).
@Composable
fun PublicarEventoScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: EventoFormularioViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Cuando el evento queda guardado, se avisa una sola vez
    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onGuardar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = if (viewModel.esEdicion) "Editar evento" else "Publicar evento", onVolver = onVolver)

        if (estado.cargando) {
            Text("Cargando...")
            return@Column
        }

        // Un RadioButton por tipo de evento
        Seccion(titulo = "Tipo") {
            TipoEvento.entries.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = estado.tipo == opcion, onClick = { viewModel.onTipoChange(opcion) })
                    Text(opcion.etiqueta(), modifier = Modifier.clickable { viewModel.onTipoChange(opcion) })
                }
            }
        }

        CampoTexto(
            valor = estado.titulo, onValorChange = viewModel::onTituloChange,
            etiqueta = "Título", error = estado.errores["titulo"]
        )
        CampoTexto(
            valor = estado.descripcion, onValorChange = viewModel::onDescripcionChange,
            etiqueta = "Descripción (opcional)", lineas = 3, error = estado.errores["descripcion"]
        )

        Seccion(titulo = "Inicio") {
            CampoTexto(
                valor = estado.fechaInicio, onValorChange = viewModel::onFechaInicioChange,
                etiqueta = "Fecha (dd/mm/aaaa)", teclado = KeyboardType.Number
            )
            CampoTexto(
                valor = estado.horaInicio, onValorChange = viewModel::onHoraInicioChange,
                etiqueta = "Hora (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["inicio"]
            )
        }

        Seccion(titulo = if (estado.exigeTermino) "Término (obligatorio)" else "Término (opcional)") {
            CampoTexto(
                valor = estado.fechaFin, onValorChange = viewModel::onFechaFinChange,
                etiqueta = "Fecha (dd/mm/aaaa)", teclado = KeyboardType.Number
            )
            CampoTexto(
                valor = estado.horaFin, onValorChange = viewModel::onHoraFinChange,
                etiqueta = "Hora (hh:mm)", teclado = KeyboardType.Number, error = estado.errores["fin"]
            )
        }

        // Error del servidor
        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { viewModel.guardar() },
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    estado.guardando -> "Guardando..."
                    viewModel.esEdicion -> "Guardar cambios"
                    else -> "Publicar"
                }
            )
        }
    }
}
