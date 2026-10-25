package cl.duoc.aurasacrapp.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.viewmodel.EscanearViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

// Escanear el QR de un recinto para registrar la visita y abrir su ficha
@Composable
fun EscanearScreen(
    onRecintoEscaneado: (Long) -> Unit,
    viewModel: EscanearViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Cuando la visita queda registrada: aviso corto y se abre la ficha del recinto
    LaunchedEffect(estado.recintoRegistrado) {
        estado.recintoRegistrado?.let { id ->
            Toast.makeText(context, "¡Visita registrada!", Toast.LENGTH_SHORT).show()
            viewModel.navegacionRealizada()
            onRecintoEscaneado(id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Encabezado(titulo = "Escanear QR")
        Icon(
            imageVector = Icons.Filled.QrCodeScanner,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Cada templo, museo y colegio de la red tiene un código QR en su entrada. " +
                "Escanéalo para ver su ficha y registrar tu visita."
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.limpiarError()
                iniciarEscaneo(context, viewModel)
            },
            enabled = !estado.procesando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.procesando) "Procesando..." else "Escanear código QR")
        }

        // Mensaje de error (código que no es de la app, recinto no disponible, etc.)
        estado.error?.let { mensaje ->
            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
        }
    }
}

// Abre el lector de códigos de Google (ML Kit). La cámara la maneja Google Play Services,
// por eso la app no necesita pedir el permiso de cámara.
private fun iniciarEscaneo(context: Context, viewModel: EscanearViewModel) {
    val opciones = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)   // solo códigos QR
        .enableAutoZoom()                            // acerca la imagen si el código está lejos
        .build()

    GmsBarcodeScanning.getClient(context, opciones)
        .startScan()
        .addOnSuccessListener { codigo -> viewModel.procesarCodigo(codigo.rawValue) }
        .addOnCanceledListener { /* el usuario cerró la cámara: no se hace nada */ }
        .addOnFailureListener { e ->
            viewModel.onErrorEscaner("No se pudo abrir el lector QR (${e.localizedMessage ?: "error desconocido"}).")
        }
}
