package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.viewmodel.MiRecintoViewModel

// El encargado edita la información de su recinto.
// Los campos vienen con los datos actuales y "Guardar cambios" muestra un aviso y vuelve atrás.
@Composable
fun EditarFichaScreen(
    onVolver: () -> Unit,
    onGuardar: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val recinto = estado.recinto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Editar ficha", onVolver = onVolver)
        if (recinto == null) {
            Text(if (estado.cargando) "Cargando..." else "Tu cuenta no tiene un recinto a cargo.")
        } else {
            FormularioFicha(recinto = recinto, onGuardar = onGuardar)
        }
    }
}

// Los campos parten con los datos actuales del recinto
@Composable
private fun FormularioFicha(recinto: RecintoConComuna, onGuardar: () -> Unit) {
    var nombre by rememberSaveable(recinto.id) { mutableStateOf(recinto.nombreOficial) }
    var calle by rememberSaveable(recinto.id) { mutableStateOf(recinto.calle) }
    var numero by rememberSaveable(recinto.id) { mutableStateOf(recinto.numeroCalle ?: "") }
    var historia by rememberSaveable(recinto.id) { mutableStateOf(recinto.descripcionHistorica ?: "") }

    CampoTexto(valor = nombre, onValorChange = { nombre = it }, etiqueta = "Nombre oficial")
    CampoTexto(valor = calle, onValorChange = { calle = it }, etiqueta = "Calle")
    CampoTexto(valor = numero, onValorChange = { numero = it }, etiqueta = "Número")
    CampoTexto(valor = historia, onValorChange = { historia = it }, etiqueta = "Historia", lineas = 5)

    Button(onClick = onGuardar, modifier = Modifier.fillMaxWidth()) {
        Text("Guardar cambios")
    }
}
