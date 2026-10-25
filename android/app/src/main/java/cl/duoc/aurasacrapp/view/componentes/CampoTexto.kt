package cl.duoc.aurasacrapp.view.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

// Campo de texto de los formularios: ocupa todo el ancho y deja un pequeño espacio abajo
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    teclado: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false,
    lineas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        singleLine = lineas == 1,
        minLines = lineas,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        visualTransformation = if (esPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}
