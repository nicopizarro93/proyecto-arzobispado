package cl.duoc.santiagosacro.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cl.duoc.santiagosacro.view.componentes.PantallaBase

// PROVISORIA: el formulario real llega en la rama A2
@Composable
fun LoginScreen(
    onIngresoExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    PantallaBase(
        titulo = "Iniciar sesión",
        descripcion = "Ingresa a tu cuenta de Santiago Sacro."
    ) {
        Button(onClick = onIngresoExitoso) { Text("Ingresar") }
        TextButton(onClick = onVolver) { Text("Volver") }
    }
}
