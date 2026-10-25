package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.aurasacrapp.ui.theme.AuraSacrappTheme

// Pantalla de bienvenida: ícono, nombre de la app, una descripción y los botones de ingreso
@Composable
fun BienvenidaScreen(onIniciarSesion: () -> Unit, onCrearCuenta: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Icon(
            imageVector = Icons.Filled.Church,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AuraSacrapp",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Templos, arte e historia de Santiago",
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Descubre templos, museos, colegios y archivos de Santiago: " +
                "conoce su historia y registra tus visitas escaneando el código QR de cada lugar."
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onIniciarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar sesión")
        }
        OutlinedButton(onClick = onCrearCuenta, modifier = Modifier.fillMaxWidth()) {
            Text("Crear una cuenta")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BienvenidaPreview() {
    AuraSacrappTheme {
        BienvenidaScreen(onIniciarSesion = {}, onCrearCuenta = {})
    }
}
