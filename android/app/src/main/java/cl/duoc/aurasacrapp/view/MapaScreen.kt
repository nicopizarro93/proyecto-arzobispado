package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.MapaViewModel

// Zona de Santiago que muestra el mapa (latitud y longitud de las esquinas)
private const val LAT_NORTE = -33.40
private const val LAT_SUR = -33.64
private const val LON_OESTE = -70.79
private const val LON_ESTE = -70.55

// Mapa de la red: un recuadro de color con un marcador por recinto, ubicado según sus coordenadas.
// Al tocar un marcador se muestra el recinto abajo, con un botón para ver su ficha.
@Composable
fun MapaScreen(
    onVolver: () -> Unit,
    onAbrirRecinto: (Long) -> Unit,
    viewModel: MapaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Mapa de la red", onVolver = onVolver)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            // Convierte latitud/longitud a una posición dentro del recuadro
            estado.recintos.forEach { recinto ->
                val x = maxWidth * ((recinto.longitud - LON_OESTE) / (LON_ESTE - LON_OESTE)).toFloat()
                val y = maxHeight * ((LAT_NORTE - recinto.latitud) / (LAT_NORTE - LAT_SUR)).toFloat()
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = recinto.nombreOficial,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .offset(x = x - 16.dp, y = y - 32.dp)  // la punta del marcador queda en el punto
                        .size(32.dp)
                        .clickable { viewModel.seleccionar(recinto.id) }
                )
            }
        }

        // Recinto elegido en el mapa
        val seleccionado = estado.seleccionado
        if (seleccionado == null) {
            Text(text = "Toca un marcador para ver el recinto.", modifier = Modifier.padding(top = 12.dp))
        } else {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Seccion(titulo = seleccionado.nombreOficial) {
                    Text("${seleccionado.tipoRecinto.etiqueta()} · ${seleccionado.comuna}")
                    Text(seleccionado.estadoApertura.etiqueta())
                    Button(onClick = { onAbrirRecinto(seleccionado.id) }) {
                        Text("Ver ficha")
                    }
                }
            }
        }
    }
}
