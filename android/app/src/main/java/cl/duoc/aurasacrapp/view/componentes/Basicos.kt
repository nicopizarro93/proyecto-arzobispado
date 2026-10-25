package cl.duoc.aurasacrapp.view.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Piezas simples que se repiten en todas las pantallas

// Título de la pantalla en color ciruela, con flecha para volver si se le pasa onVolver
@Composable
fun Encabezado(titulo: String, onVolver: (() -> Unit)? = null) {
    if (onVolver != null) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
    }
    Text(
        text = titulo,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
}

// Tarjeta de color suave con un título: agrupa una parte de la pantalla
@Composable
fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

// Tarjeta blanca que se puede tocar: para cada elemento de una lista (recinto, evento, ruta...)
@Composable
fun Item(titulo: String, detalle: String, onClick: (() -> Unit)? = null) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier) { ItemTextos(titulo, detalle) }
    } else {
        Card(modifier = modifier) { ItemTextos(titulo, detalle) }
    }
}

@Composable
private fun ItemTextos(titulo: String, detalle: String) {
    Column(modifier = Modifier.padding(12.dp)) {
        Text(text = titulo, style = MaterialTheme.typography.titleMedium)
        Text(text = detalle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
