package cl.duoc.santiagosacro.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import cl.duoc.santiagosacro.view.componentes.CampoTexto

// Crear cuenta. En esta entrega el formulario solo se completa y vuelve al login;
// en la prueba 3 se enviará al backend (POST /api/auth/registro).
@OptIn(ExperimentalMaterial3Api::class)
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
    var repetirPassword by rememberSaveable { mutableStateOf("") }
    var aceptaPrivacidad by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Regístrate para planificar visitas y guardar los lugares que conoces.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tipo de cuenta
        Text(text = "Tipo de cuenta", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val colores = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            FilterChip(
                selected = !esDocente,
                onClick = { esDocente = false },
                label = { Text("Visitante") },
                colors = colores
            )
            FilterChip(
                selected = esDocente,
                onClick = { esDocente = true },
                label = { Text("Docente") },
                colors = colores
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoTexto(valor = nombre, onValorChange = { nombre = it }, etiqueta = "Nombre")
            CampoTexto(valor = apellido, onValorChange = { apellido = it }, etiqueta = "Apellido")
            if (esDocente) {
                CampoTexto(
                    valor = institucion,
                    onValorChange = { institucion = it },
                    etiqueta = "Colegio o institución"
                )
            }
            CampoTexto(
                valor = correo,
                onValorChange = { correo = it },
                etiqueta = "Correo",
                teclado = KeyboardType.Email
            )
            CampoTexto(
                valor = password,
                onValorChange = { password = it },
                etiqueta = "Contraseña",
                teclado = KeyboardType.Password,
                esPassword = true
            )
            CampoTexto(
                valor = repetirPassword,
                onValorChange = { repetirPassword = it },
                etiqueta = "Repetir contraseña",
                teclado = KeyboardType.Password,
                esPassword = true
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptaPrivacidad, onCheckedChange = { aceptaPrivacidad = it })
            Text(text = "Acepto la política de privacidad", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCuentaCreada,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text = "Crear cuenta", fontSize = 16.sp)
        }
        TextButton(
            onClick = onIrALogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
