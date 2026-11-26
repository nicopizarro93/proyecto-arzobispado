package cl.duoc.aurasacrapp.view

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.TipoRecinto
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Item
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.AdminEncargadosViewModel
import cl.duoc.aurasacrapp.viewmodel.AdminRecintosViewModel

// Panel del administrador de la red: registrar y dar de baja recintos (HU-35) y gestionar las
// cuentas de los encargados (HU-34)
@Composable
fun AdminScreen(
    onVolver: () -> Unit,
    recintos: AdminRecintosViewModel = viewModel(),
    encargados: AdminEncargadosViewModel = viewModel()
) {
    var seccion by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Administración de la red", onVolver = onVolver)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = seccion == 0, onClick = { seccion = 0 }, label = { Text("Recintos") })
            FilterChip(selected = seccion == 1, onClick = { seccion = 1 }, label = { Text("Encargados") })
        }

        if (seccion == 0) SeccionRecintos(recintos) else SeccionEncargados(encargados)
    }
}

@Composable
private fun SeccionRecintos(viewModel: AdminRecintosViewModel) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    // ---- Registrar un recinto ----
    Seccion(titulo = "Registrar un recinto") {
        // Tipo de recinto (uno solo)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TipoRecinto.entries.forEach { tipo ->
                FilterChip(
                    selected = estado.tipo == tipo,
                    onClick = { viewModel.onTipoChange(tipo) },
                    label = { Text(tipo.etiqueta()) }
                )
            }
        }
        CampoTexto(
            valor = estado.nombre, onValorChange = viewModel::onNombreChange,
            etiqueta = "Nombre oficial", error = estado.errores["nombre"]
        )
        Selector(
            etiqueta = "Comuna",
            elegido = estado.comuna?.nombre,
            opciones = estado.comunas.map { it.nombre },
            onElegir = { indice -> viewModel.onComunaChange(estado.comunas[indice]) },
            error = estado.errores["comuna"]
        )
        CampoTexto(
            valor = estado.calle, onValorChange = viewModel::onCalleChange,
            etiqueta = "Calle", error = estado.errores["calle"]
        )
        CampoTexto(
            valor = estado.numero, onValorChange = viewModel::onNumeroChange,
            etiqueta = "Número (opcional)", error = estado.errores["numero"]
        )
        CampoTexto(
            valor = estado.latitud, onValorChange = viewModel::onLatitudChange,
            etiqueta = "Latitud (ej. -33.4489)", error = estado.errores["latitud"]
        )
        CampoTexto(
            valor = estado.longitud, onValorChange = viewModel::onLongitudChange,
            etiqueta = "Longitud (ej. -70.6693)", error = estado.errores["longitud"]
        )
        CampoTexto(
            valor = estado.historia, onValorChange = viewModel::onHistoriaChange,
            etiqueta = "Reseña histórica (opcional)", lineas = 3, error = estado.errores["historia"]
        )
        estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = { viewModel.registrar() },
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (estado.guardando) "Guardando..." else "Registrar recinto") }
        Text("El recinto nace cerrado: su encargado lo abre cuando corresponda.")
    }

    // ---- Recintos de la red: dar de baja ----
    Text(
        text = "Recintos de la red",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
    estado.recintos.forEach { recinto ->
        Item(
            titulo = recinto.nombreOficial,
            detalle = "${recinto.tipoRecinto.etiqueta()} · ${recinto.comuna} · ${recinto.estadoApertura.etiqueta()}"
        )
        if (estado.confirmandoBajaId == recinto.id) {
            // La baja se confirma antes de hacerse: el recinto deja de verse en la app
            Text("¿Dar de baja \"${recinto.nombreOficial}\"? Dejará de verse en la app, pero su historial se conserva.")
            Row {
                Button(onClick = { viewModel.confirmarBaja() }, enabled = !estado.guardando) { Text("Sí, dar de baja") }
                OutlinedButton(
                    onClick = { viewModel.cancelarBaja() },
                    modifier = Modifier.padding(start = 8.dp)
                ) { Text("Cancelar") }
            }
        } else {
            OutlinedButton(onClick = { viewModel.pedirBaja(recinto.id) }) { Text("Dar de baja") }
        }
    }
}

@Composable
private fun SeccionEncargados(viewModel: AdminEncargadosViewModel) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    // ---- Cuentas de encargados ----
    Text(
        text = "Cuentas de encargados",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
    estado.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
    when {
        estado.cargando -> Text("Cargando...")
        estado.encargados.isEmpty() -> Text("Aún no hay cuentas de encargados.")
        else -> estado.encargados.forEach { encargado ->
            Item(
                titulo = "${encargado.nombre} ${encargado.apellido}",
                detalle = "${encargado.correo}\n${encargado.recinto ?: "Sin recinto"} · " +
                    if (encargado.activo) "Cuenta activa" else "Cuenta desactivada"
            )
            OutlinedButton(onClick = { viewModel.cambiarActivo(encargado) }, enabled = !estado.guardando) {
                Text(if (encargado.activo) "Desactivar cuenta" else "Activar cuenta")
            }
        }
    }

    // ---- Cuenta nueva ----
    Seccion(titulo = "Crear una cuenta de encargado") {
        CampoTexto(
            valor = estado.nombre, onValorChange = viewModel::onNombreChange,
            etiqueta = "Nombre", error = estado.errores["nombre"]
        )
        CampoTexto(
            valor = estado.apellido, onValorChange = viewModel::onApellidoChange,
            etiqueta = "Apellido", error = estado.errores["apellido"]
        )
        CampoTexto(
            valor = estado.correo, onValorChange = viewModel::onCorreoChange,
            etiqueta = "Correo", teclado = KeyboardType.Email, error = estado.errores["correo"]
        )
        CampoTexto(
            valor = estado.password, onValorChange = viewModel::onPasswordChange,
            etiqueta = "Contraseña inicial", teclado = KeyboardType.Password, esPassword = true,
            error = estado.errores["password"]
        )
        Selector(
            etiqueta = "Recinto a cargo",
            elegido = estado.recinto?.nombreOficial,
            opciones = estado.recintos.map { it.nombreOficial },
            onElegir = { indice -> viewModel.onRecintoChange(estado.recintos[indice]) },
            error = estado.errores["recinto"]
        )
        Button(
            onClick = { viewModel.crear() },
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (estado.guardando) "Guardando..." else "Crear cuenta") }
    }
}

// Lista desplegable para elegir una opción (la comuna de un recinto, el recinto de un encargado...)
@Composable
private fun Selector(
    etiqueta: String,
    elegido: String?,
    opciones: List<String>,
    onElegir: (Int) -> Unit,
    error: String? = null
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(bottom = 8.dp)) {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Text(elegido ?: "$etiqueta ▾")
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEachIndexed { indice, texto ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = {
                        onElegir(indice)
                        abierto = false
                    }
                )
            }
        }
    }
    error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
}
