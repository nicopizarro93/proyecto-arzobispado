package cl.duoc.santiagosacro.view

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.util.formatearDuracion
import cl.duoc.santiagosacro.view.componentes.etiqueta
import cl.duoc.santiagosacro.view.componentes.icono
import cl.duoc.santiagosacro.viewmodel.RutaResumen
import cl.duoc.santiagosacro.viewmodel.RutasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutasScreen(
    onVolver: () -> Unit,
    onAbrirRuta: (Long) -> Unit,
    onCrearRuta: () -> Unit,
    viewModel: RutasViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutas sugeridas") },
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
        // Botón flotante para armar una ruta propia
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrearRuta,
                icon = { Icon(Icons.Filled.AddRoad, contentDescription = null) },
                text = { Text("Crear mi ruta") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            // Espacio abajo para que el botón flotante no tape la última ruta
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Recorridos preparados por la red patrimonial, con el tiempo estimado de visita y traslado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(viewModel.rutas, key = { it.ruta.id }) { resumen ->
                RutaCard(resumen = resumen, onClick = { onAbrirRuta(resumen.ruta.id) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RutaCard(resumen: RutaResumen, onClick: () -> Unit) {
    val ruta = resumen.ruta
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ruta.nombre,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ruta.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DatoRuta(icono = ruta.modoTraslado.icono(), texto = ruta.modoTraslado.etiqueta())
                DatoRuta(icono = Icons.Filled.Place, texto = "${ruta.paradas.size} paradas")
                DatoRuta(icono = Icons.Filled.Schedule, texto = formatearDuracion(resumen.minutosTotales))
            }
        }
    }
}

@Composable
private fun DatoRuta(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = texto, style = MaterialTheme.typography.labelMedium)
    }
}
