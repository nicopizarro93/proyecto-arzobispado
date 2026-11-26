package cl.duoc.aurasacrapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.view.componentes.CampoTexto
import cl.duoc.aurasacrapp.view.componentes.Encabezado
import cl.duoc.aurasacrapp.view.componentes.Seccion
import cl.duoc.aurasacrapp.view.componentes.etiqueta
import cl.duoc.aurasacrapp.viewmodel.PerfilViewModel

// Datos de la cuenta, edición del perfil, cambio de contraseña, acceso a "Mi recinto" (solo encargados)
// y cerrar sesión
@Composable
fun PerfilScreen(
    onCerrarSesion: () -> Unit,
    onMiRecinto: () -> Unit,
    onAdministrar: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val usuario = estado.usuario

    // Aviso corto de lo que se guardó
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Encabezado(titulo = "Mi perfil")

        if (usuario != null) {
            Seccion(titulo = "${usuario.nombre} ${usuario.apellido}") {
                Text("Correo: ${usuario.correo}")
                Text("Tipo de cuenta: ${usuario.rol.etiqueta()}")
                usuario.institucion?.let { Text("Institución: $it") }
            }

            // Acceso directo al panel del encargado
            if (usuario.rol == Rol.ENCARGADO) {
                Button(onClick = onMiRecinto, modifier = Modifier.fillMaxWidth()) {
                    Text("Administrar mi recinto")
                }
            }

            // Solo para el administrador de la red: recintos y cuentas de encargados
            if (usuario.rol == Rol.ADMINISTRADOR) {
                Button(onClick = onAdministrar, modifier = Modifier.fillMaxWidth()) {
                    Text("Administrar la red")
                }
            }

            // Arriba, para encontrarlo sin bajar por los formularios
            OutlinedButton(
                onClick = {
                    viewModel.cerrarSesion()
                    onCerrarSesion()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }

            // ---- Editar mis datos (el correo no se cambia) ----
            Seccion(titulo = "Editar mis datos") {
                CampoTexto(
                    valor = estado.nombre, onValorChange = viewModel::onNombreChange,
                    etiqueta = "Nombre", error = estado.erroresDatos["nombre"]
                )
                CampoTexto(
                    valor = estado.apellido, onValorChange = viewModel::onApellidoChange,
                    etiqueta = "Apellido", error = estado.erroresDatos["apellido"]
                )
                // Solo los docentes indican su colegio
                if (estado.esDocente) {
                    CampoTexto(
                        valor = estado.institucion, onValorChange = viewModel::onInstitucionChange,
                        etiqueta = "Colegio o institución", error = estado.erroresDatos["institucion"]
                    )
                }
                estado.errorDatos?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = { viewModel.guardarDatos() },
                    enabled = !estado.guardandoDatos,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (estado.guardandoDatos) "Guardando..." else "Guardar datos")
                }
            }

            // ---- Cambiar contraseña ----
            Seccion(titulo = "Cambiar contraseña") {
                CampoTexto(
                    valor = estado.passwordActual, onValorChange = viewModel::onPasswordActualChange,
                    etiqueta = "Contraseña actual", teclado = KeyboardType.Password, esPassword = true,
                    error = estado.erroresPassword["actual"]
                )
                CampoTexto(
                    valor = estado.passwordNueva, onValorChange = viewModel::onPasswordNuevaChange,
                    etiqueta = "Contraseña nueva", teclado = KeyboardType.Password, esPassword = true,
                    error = estado.erroresPassword["nueva"]
                )
                CampoTexto(
                    valor = estado.confirmarPassword, onValorChange = viewModel::onConfirmarChange,
                    etiqueta = "Confirmar contraseña nueva", teclado = KeyboardType.Password, esPassword = true,
                    error = estado.erroresPassword["confirmar"]
                )
                estado.errorPassword?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = { viewModel.cambiarPassword() },
                    enabled = !estado.guardandoPassword,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (estado.guardandoPassword) "Guardando..." else "Cambiar contraseña")
                }
            }
        }
    }
}
