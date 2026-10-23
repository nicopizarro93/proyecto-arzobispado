package cl.duoc.santiagosacro.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.santiagosacro.model.EstadoApertura
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.view.componentes.EstadoChip
import cl.duoc.santiagosacro.view.componentes.descripcion
import cl.duoc.santiagosacro.view.componentes.etiqueta
import cl.duoc.santiagosacro.viewmodel.MiRecintoUiState
import cl.duoc.santiagosacro.viewmodel.MiRecintoViewModel

// Pantalla del encargado: cambia el estado de apertura de su recinto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiRecintoScreen(
    onVolver: () -> Unit,
    onVerFicha: (Long) -> Unit,
    onPublicarEvento: () -> Unit,
    onEditarFicha: () -> Unit,
    onGuias: () -> Unit,
    viewModel: MiRecintoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Aviso corto cuando se guarda el cambio
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let { texto ->
            Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi recinto") },
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
        val recinto = estado.recinto
        when {
            estado.cargando -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            recinto == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu cuenta no tiene un recinto a cargo.", style = MaterialTheme.typography.bodyLarge)
            }
            else -> MiRecintoContenido(
                recinto = recinto,
                estado = estado,
                onElegir = viewModel::elegirEstado,
                onGuardar = viewModel::guardar,
                onVerFicha = onVerFicha,
                onPublicarEvento = onPublicarEvento,
                onEditarFicha = onEditarFicha,
                onGuias = onGuias,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun MiRecintoContenido(
    recinto: RecintoConComuna,
    estado: MiRecintoUiState,
    onElegir: (EstadoApertura) -> Unit,
    onGuardar: () -> Unit,
    onVerFicha: (Long) -> Unit,
    onPublicarEvento: () -> Unit,
    onEditarFicha: () -> Unit,
    onGuias: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(text = recinto.nombreOficial, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "${recinto.tipoRecinto.etiqueta()} · ${recinto.comuna}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Estado actual",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            EstadoChip(estado = recinto.estadoApertura)
        }
        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Cambiar estado de apertura", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Los visitantes verán el nuevo estado al instante en la lista y en la ficha.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EstadoApertura.entries.forEach { opcion ->
                OpcionEstado(
                    opcion = opcion,
                    seleccionada = estado.estadoElegido == opcion,
                    onClick = { onElegir(opcion) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGuardar,
            // Solo se puede guardar si eligió un estado distinto al actual
            enabled = !estado.guardando && estado.estadoElegido != recinto.estadoApertura,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (estado.guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Guardar estado", fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(28.dp))

        // Panel del encargado: todo lo que administra de su recinto
        Text(text = "Administración", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                AccionEncargado(
                    icono = Icons.Filled.Edit,
                    titulo = "Editar ficha y horarios",
                    detalle = "Nombre, dirección, historia y horario de atención",
                    onClick = onEditarFicha
                )
                HorizontalDivider()
                AccionEncargado(
                    icono = Icons.Filled.Campaign,
                    titulo = "Publicar evento o aviso",
                    detalle = "Incluye aperturas y cierres excepcionales",
                    onClick = onPublicarEvento
                )
                HorizontalDivider()
                AccionEncargado(
                    icono = Icons.Filled.Groups,
                    titulo = "Guías y solicitudes",
                    detalle = "Disponibilidad de guías y pedidos de colegios",
                    onClick = onGuias
                )
                HorizontalDivider()
                AccionEncargado(
                    icono = Icons.Filled.Visibility,
                    titulo = "Ver ficha pública",
                    detalle = "Cómo la ven los visitantes",
                    onClick = { onVerFicha(recinto.id) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Una fila del panel del encargado
@Composable
private fun AccionEncargado(icono: ImageVector, titulo: String, detalle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = detalle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Una opción de estado con su botón de radio y su explicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpcionEstado(opcion: EstadoApertura, seleccionada: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (seleccionada) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (seleccionada) 2.dp else 1.dp,
            color = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = seleccionada, onClick = onClick)
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = opcion.etiqueta(),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = opcion.descripcion(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
