package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.TipoEvento
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.MiRecintoViewModel

// El encargado publica un evento o aviso de su recinto. El formulario
// es solo visual: "Publicar" muestra un aviso y vuelve a "Mi recinto".
@Composable
fun PublicarEventoScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var tipo by rememberSaveable { mutableStateOf(TipoEvento.CONCIERTO) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Publicar evento", onVolver = onVolver)
        estado.recinto?.let { Text("Se publicará en ${it.nombreOficial}") }

        // Un RadioButton por tipo de evento
        Seccion(titulo = "Tipo") {
            TipoEvento.entries.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipo == opcion, onClick = { tipo = opcion })
                    Text(opcion.etiqueta())
                }
            }
        }

        CampoTexto(valor = titulo, onValorChange = { titulo = it }, etiqueta = "Título")
        CampoTexto(valor = descripcion, onValorChange = { descripcion = it }, etiqueta = "Descripción", lineas = 3)
        CampoTexto(valor = fecha, onValorChange = { fecha = it }, etiqueta = "Fecha (dd/mm/aaaa)", teclado = KeyboardType.Number)
        CampoTexto(valor = hora, onValorChange = { hora = it }, etiqueta = "Hora (hh:mm)", teclado = KeyboardType.Number)

        Button(onClick = onGuardar, modifier = Modifier.fillMaxWidth()) {
            Text("Publicar")
        }
    }
}
