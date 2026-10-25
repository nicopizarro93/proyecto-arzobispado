package cl.duoc.aurasacrapp.view

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion

// Crear cuenta. El formulario es solo visual:
// "Crear cuenta" muestra un aviso y lleva al login.
@Composable
fun RegistroScreen(
    onCuentaCreada: () -> Unit,
    onIrALogin: () -> Unit,
    onVolver: () -> Unit
) {
    var esDocente by rememberSaveable { mutableStateOf(false) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var institucion by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var aceptaPrivacidad by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Crear cuenta", onVolver = onVolver)

        Seccion(titulo = "Tipo de cuenta") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = !esDocente, onClick = { esDocente = false })
                Text("Visitante")
                RadioButton(selected = esDocente, onClick = { esDocente = true })
                Text("Docente")
            }
        }

        CampoTexto(valor = nombre, onValorChange = { nombre = it }, etiqueta = "Nombre")
        CampoTexto(valor = apellido, onValorChange = { apellido = it }, etiqueta = "Apellido")
        // Solo los docentes indican su colegio
        if (esDocente) {
            CampoTexto(valor = institucion, onValorChange = { institucion = it }, etiqueta = "Colegio o institución")
        }
        CampoTexto(valor = correo, onValorChange = { correo = it }, etiqueta = "Correo", teclado = KeyboardType.Email)
        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            etiqueta = "Contraseña",
            teclado = KeyboardType.Password,
            esPassword = true
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptaPrivacidad, onCheckedChange = { aceptaPrivacidad = it })
            Text("Acepto la política de privacidad")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onCuentaCreada, modifier = Modifier.fillMaxWidth()) {
            Text("Crear cuenta")
        }
        TextButton(onClick = onIrALogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
