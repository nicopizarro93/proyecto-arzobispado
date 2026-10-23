package cl.duoc.santiagosacro.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.model.ModoTraslado
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.util.formatearDuracion
import cl.duoc.santiagosacro.view.componentes.CampoTexto
import cl.duoc.santiagosacro.view.componentes.etiqueta
import cl.duoc.santiagosacro.view.componentes.icono
import cl.duoc.santiagosacro.viewmodel.CrearRutaViewModel

// Crear una ruta propia: elegir recintos en orden y el modo de traslado.
// El tiempo estimado se calcula al instante; "Guardar ruta" se conectará al backend en la prueba 3.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRutaScreen(
    onVolver: () -> Unit,
    viewModel: CrearRutaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var nombre by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear ruta") },
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
        bottomBar = {
            // Resumen fijo abajo: paradas, tiempo estimado y botón para guardar
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${estado.seleccionados.size} paradas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (estado.seleccionados.isEmpty()) "Elige recintos" else formatearDuracion(estado.minutosTotales),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Button(
                        onClick = onVolver,
                        enabled = estado.seleccionados.size >= 2,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(text = "Guardar ruta", fontSize = 16.sp)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                CampoTexto(valor = nombre, onValorChange = { nombre = it }, etiqueta = "Nombre de la ruta")
            }
            item {
                Column {
                Text(text = "¿Cómo te mueves?", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModoTraslado.entries.forEach { opcion ->
                        FilterChip(
                            selected = estado.modo == opcion,
                            onClick = { viewModel.elegirModo(opcion) },
                            label = { Text(opcion.etiqueta()) },
                            leadingIcon = {
                                Icon(opcion.icono(), contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                }
            }
            item {
                Text(
                    text = "Elige los recintos en el orden en que los visitarás",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            items(estado.recintos, key = { it.id }) { recinto ->
                val posicion = estado.seleccionados.indexOf(recinto.id)
                RecintoParaRuta(
                    recinto = recinto,
                    orden = if (posicion >= 0) posicion + 1 else null,
                    onClick = { viewModel.alternar(recinto.id) }
                )
            }
        }
    }
}

// Fila de un recinto: si está elegido muestra su número de parada
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecintoParaRuta(recinto: RecintoConComuna, orden: Int?, onClick: () -> Unit) {
    val elegido = orden != null
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (elegido) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (elegido) 2.dp else 1.dp,
            color = if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con el número de parada (vacío si no está elegido)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (orden != null) {
                    Text(
                        text = orden.toString(),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = recinto.nombreOficial,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "${recinto.tipoRecinto.etiqueta()} · ${recinto.comuna}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
