package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.Guia
import cl.duoc.aurasacrapp.model.SolicitudGuia
import cl.duoc.aurasacrapp.repository.GuiaRepository
import cl.duoc.aurasacrapp.viewmodel.MiRecintoViewModel

// El encargado administra la disponibilidad de sus guías y responde las solicitudes de colegios.
// En esta entrega los cambios se ven solo en pantalla; en la prueba 3 se guardarán en el backend.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiasScreen(
    onVolver: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val recintoId = estado.recinto?.id
    val guias = if (recintoId != null) GuiaRepository.guiasDe(recintoId) else emptyList()
    val solicitudes = if (recintoId != null) GuiaRepository.solicitudesDe(recintoId) else emptyList()

    // Estado solo de pantalla: guías activos y respuesta a cada solicitud
    val inactivos = remember { mutableStateMapOf<Long, Boolean>() }
    val respuestas = remember { mutableStateMapOf<Long, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guías y solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = "Guías del recinto", style = MaterialTheme.typography.titleMedium)
            }
            if (guias.isEmpty()) {
                item { TextoVacio("Este recinto todavía no tiene guías.") }
            }
            items(guias, key = { it.id }) { guia ->
                GuiaCard(
                    guia = guia,
                    activo = inactivos[guia.id] != true,
                    onCambiarActivo = { activo -> inactivos[guia.id] = !activo }
                )
            }
            item {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar bloque de disponibilidad")
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Solicitudes de colegios", style = MaterialTheme.typography.titleMedium)
            }
            if (solicitudes.isEmpty()) {
                item { TextoVacio("No hay solicitudes pendientes.") }
            }
            items(solicitudes, key = { it.id }) { solicitud ->
                SolicitudCard(
                    solicitud = solicitud,
                    respuesta = respuestas[solicitud.id],
                    onResponder = { texto -> respuestas[solicitud.id] = texto }
                )
            }
        }
    }
}

@Composable
private fun TextoVacio(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun GuiaCard(guia: Guia, activo: Boolean, onCambiarActivo: (Boolean) -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con las iniciales
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${guia.nombre.first()}${guia.apellido.first()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${guia.nombre} ${guia.apellido}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                guia.disponibilidad.forEach { bloque ->
                    Text(
                        text = bloque,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(checked = activo, onCheckedChange = onCambiarActivo)
        }
    }
}

@Composable
private fun SolicitudCard(solicitud: SolicitudGuia, respuesta: String?, onResponder: (String) -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = solicitud.institucion,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "${solicitud.curso} · ${solicitud.personas} personas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = solicitud.fecha,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (respuesta == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onResponder("Rechazada") }, shape = RoundedCornerShape(12.dp)) {
                        Text("Rechazar")
                    }
                    Button(onClick = { onResponder("Guía asignado") }, shape = RoundedCornerShape(12.dp)) {
                        Text("Asignar guía")
                    }
                }
            } else {
                Surface(
                    color = if (respuesta == "Rechazada") {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = respuesta,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
