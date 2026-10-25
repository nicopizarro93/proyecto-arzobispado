package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onIngresoExitoso: () -> Unit,
    onVolver: () -> Unit,
    onCrearCuenta: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    // La pantalla solo dibuja el estado que le entrega el ViewModel
    val estado by viewModel.uiState.collectAsState()

    // Cuando el ingreso sale bien, se navega una sola vez
    LaunchedEffect(estado.ingresoExitoso) {
        if (estado.ingresoExitoso) onIngresoExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Iniciar sesión", onVolver = onVolver)

        CampoTexto(
            valor = estado.correo,
            onValorChange = viewModel::onCorreoChange,
            etiqueta = "Correo",
            teclado = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(8.dp))
        CampoTexto(
            valor = estado.password,
            onValorChange = viewModel::onPasswordChange,
            etiqueta = "Contraseña",
            teclado = KeyboardType.Password,
            esPassword = true
        )

        // Mensaje de error (solo si hay)
        estado.error?.let { mensaje ->
            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.ingresar() },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.cargando) "Ingresando..." else "Ingresar")
        }
        TextButton(onClick = onCrearCuenta) {
            Text("¿No tienes cuenta? Crear una cuenta")
        }
    }
}
