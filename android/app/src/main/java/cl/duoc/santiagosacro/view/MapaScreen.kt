package cl.duoc.santiagosacro.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.model.EstadoApertura
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.view.componentes.EstadoChip
import cl.duoc.santiagosacro.view.componentes.etiqueta
import cl.duoc.santiagosacro.viewmodel.MapaViewModel
import kotlin.math.roundToInt

// Zona de Santiago que muestra el mapa (latitud y longitud de las esquinas)
private const val LAT_NORTE = -33.40
private const val LAT_SUR = -33.64
private const val LON_OESTE = -70.79
private const val LON_ESTE = -70.55

// Mapa de la red: en esta entrega es un mapa esquemático dibujado con Canvas,
// con cada recinto ubicado según sus coordenadas. En la prueba 3 se reemplaza por un mapa real.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    onVolver: () -> Unit,
    onAbrirRecinto: (Long) -> Unit,
    viewModel: MapaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de la red") },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Accesos rápidos: tocar un nombre marca el recinto en el mapa
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.recintos, key = { it.id }) { recinto ->
                    FilterChip(
                        selected = recinto.id == estado.seleccionado?.id,
                        onClick = { viewModel.seleccionar(recinto.id) },
                        label = { Text(recinto.nombreOficial) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                MapaEsquematico(
                    recintos = estado.recintos,
                    seleccionadoId = estado.seleccionado?.id,
                    onSeleccionar = viewModel::seleccionar,
                    modifier = Modifier.fillMaxSize()
                )
                val seleccionado = estado.seleccionado
                if (seleccionado == null) {
                    Leyenda(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))
                } else {
                    TarjetaRecintoMapa(
                        recinto = seleccionado,
                        onVerFicha = { onAbrirRecinto(seleccionado.id) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MapaEsquematico(
    recintos: List<RecintoConComuna>,
    seleccionadoId: Long?,
    onSeleccionar: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    val calle = MaterialTheme.colorScheme.outlineVariant
    val avenida = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    val rio = MaterialTheme.colorScheme.tertiaryContainer

    BoxWithConstraints(
        modifier = modifier
            .background(fondo)
            .clickable { onSeleccionar(null) }      // tocar el fondo quita la selección
    ) {
        val anchoPx = constraints.maxWidth.toFloat()
        val altoPx = constraints.maxHeight.toFloat()
        val densidad = LocalDensity.current

        // Fondo: cuadrícula de calles, dos avenidas y el río Mapocho
        Canvas(modifier = Modifier.fillMaxSize()) {
            val paso = size.width / 6f
            var x = paso
            while (x < size.width) {
                drawLine(calle, Offset(x, 0f), Offset(x, size.height), strokeWidth = 2f)
                x += paso
            }
            var y = paso
            while (y < size.height) {
                drawLine(calle, Offset(0f, y), Offset(size.width, y), strokeWidth = 2f)
                y += paso
            }
            drawLine(avenida, Offset(0f, size.height * 0.55f), Offset(size.width, size.height * 0.40f), strokeWidth = 10f)
            drawLine(avenida, Offset(size.width * 0.60f, 0f), Offset(size.width * 0.70f, size.height), strokeWidth = 10f)
            val mapocho = Path().apply {
                moveTo(0f, size.height * 0.22f)
                cubicTo(
                    size.width * 0.30f, size.height * 0.10f,
                    size.width * 0.60f, size.height * 0.24f,
                    size.width, size.height * 0.08f
                )
            }
            drawPath(mapocho, color = rio, style = Stroke(width = 18f, cap = StrokeCap.Round))
        }

        // Un marcador por recinto, ubicado con sus coordenadas
        recintos.forEach { recinto ->
            val px = ((recinto.longitud - LON_OESTE) / (LON_ESTE - LON_OESTE) * anchoPx).toFloat()
            val py = ((LAT_NORTE - recinto.latitud) / (LAT_NORTE - LAT_SUR) * altoPx).toFloat()
            val elegido = recinto.id == seleccionadoId
            val tamano = if (elegido) 46.dp else 34.dp
            val tamanoPx = with(densidad) { tamano.toPx() }
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = recinto.nombreOficial,
                tint = if (elegido) MaterialTheme.colorScheme.primary else colorEstado(recinto.estadoApertura),
                modifier = Modifier
                    .offset { IntOffset((px - tamanoPx / 2).roundToInt(), (py - tamanoPx).roundToInt()) }
                    .size(tamano)
                    .clickable { onSeleccionar(recinto.id) }
            )
        }
    }
}

@Composable
private fun colorEstado(estado: EstadoApertura): Color = when (estado) {
    EstadoApertura.ABIERTO -> MaterialTheme.colorScheme.tertiary
    EstadoApertura.CERRADO -> MaterialTheme.colorScheme.onSurfaceVariant
    EstadoApertura.EN_CELEBRACION -> MaterialTheme.colorScheme.secondary
}

// Explica el color de cada marcador
@Composable
private fun Leyenda(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EstadoApertura.entries.forEach { estado ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colorEstado(estado))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = estado.etiqueta(), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// Tarjeta del recinto elegido, con acceso a su ficha
@Composable
private fun TarjetaRecintoMapa(
    recinto: RecintoConComuna,
    onVerFicha: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recinto.nombreOficial,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp)
                )
                Text(
                    text = "${recinto.tipoRecinto.etiqueta()} · ${recinto.comuna}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                EstadoChip(estado = recinto.estadoApertura)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = onVerFicha, shape = RoundedCornerShape(12.dp)) {
                Text("Ver ficha")
            }
        }
    }
}
