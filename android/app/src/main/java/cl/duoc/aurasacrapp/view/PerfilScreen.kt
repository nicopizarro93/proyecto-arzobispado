package cl.duoc.aurasacrapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.PerfilViewModel

// Datos de la cuenta, acceso a "Mi recinto" (solo encargados) y cerrar sesión
@Composable
fun PerfilScreen(
    onCerrarSesion: () -> Unit,
    onMiRecinto: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Mi perfil")

        usuario?.let { u ->
            Seccion(titulo = "${u.nombre} ${u.apellido}") {
                Text("Correo: ${u.correo}")
                Text("Tipo de cuenta: ${u.rol.etiqueta()}")
                u.institucion?.let { Text("Institución: $it") }
            }

            // Acceso directo al panel del encargado
            if (u.rol == Rol.ENCARGADO) {
                Button(onClick = onMiRecinto, modifier = Modifier.fillMaxWidth()) {
                    Text("Administrar mi recinto")
                }
            }
        }

        OutlinedButton(
            onClick = {
                viewModel.cerrarSesion()
                onCerrarSesion()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
