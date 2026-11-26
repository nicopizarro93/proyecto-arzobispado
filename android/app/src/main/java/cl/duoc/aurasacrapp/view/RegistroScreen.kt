package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.viewmodel.RegistroViewModel

// Crear cuenta: valida el formulario y crea la cuenta en el backend
@Composable
fun RegistroScreen(
    onCuentaCreada: () -> Unit,
    onIrALogin: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Cuando la cuenta queda creada, se avisa una sola vez
    LaunchedEffect(estado.cuentaCreada) {
        if (estado.cuentaCreada) onCuentaCreada()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Crear cuenta", onVolver = onVolver)

        Seccion(titulo = "Tipo de cuenta") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = !estado.esDocente, onClick = { viewModel.onTipoCuentaChange(false) })
                Text("Visitante", modifier = Modifier.clickable { viewModel.onTipoCuentaChange(false) })
                RadioButton(selected = estado.esDocente, onClick = { viewModel.onTipoCuentaChange(true) })
                Text("Docente", modifier = Modifier.clickable { viewModel.onTipoCuentaChange(true) })
            }
        }

        CampoTexto(
            valor = estado.nombre, onValorChange = viewModel::onNombreChange,
            etiqueta = "Nombre", error = estado.errores["nombre"]
        )
        CampoTexto(
            valor = estado.apellido, onValorChange = viewModel::onApellidoChange,
            etiqueta = "Apellido", error = estado.errores["apellido"]
        )
        // Solo los docentes indican su colegio
        if (estado.esDocente) {
            CampoTexto(
                valor = estado.institucion, onValorChange = viewModel::onInstitucionChange,
                etiqueta = "Colegio o institución", error = estado.errores["institucion"]
            )
        }
        CampoTexto(
            valor = estado.correo, onValorChange = viewModel::onCorreoChange,
            etiqueta = "Correo", teclado = KeyboardType.Email, error = estado.errores["correo"]
        )
        CampoTexto(
            valor = estado.password, onValorChange = viewModel::onPasswordChange,
            etiqueta = "Contraseña", teclado = KeyboardType.Password, esPassword = true,
            error = estado.errores["password"]
        )
        CampoTexto(
            valor = estado.confirmarPassword, onValorChange = viewModel::onConfirmarChange,
            etiqueta = "Confirmar contraseña", teclado = KeyboardType.Password, esPassword = true,
            error = estado.errores["confirmar"]
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = estado.aceptaPrivacidad, onCheckedChange = viewModel::onPrivacidadChange)
            Text(
                "Acepto la política de privacidad",
                modifier = Modifier.clickable { viewModel.onPrivacidadChange(!estado.aceptaPrivacidad) }
            )
        }

        // Error del servidor (por ejemplo, correo ya registrado)
        estado.error?.let { mensaje ->
            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))
        // El botón solo se habilita después de aceptar la política de privacidad
        Button(
            onClick = { viewModel.crearCuenta() },
            enabled = estado.aceptaPrivacidad && !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.cargando) "Creando cuenta..." else "Crear cuenta")
        }
        TextButton(onClick = onIrALogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
