package cl.duoc.santiagosacro.view.componentes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.model.EstadoApertura
import cl.duoc.santiagosacro.ui.theme.GrisCerrado
import cl.duoc.santiagosacro.ui.theme.Oro
import cl.duoc.santiagosacro.ui.theme.OroClaro
import cl.duoc.santiagosacro.ui.theme.SuperficieVariante
import cl.duoc.santiagosacro.ui.theme.Verde
import cl.duoc.santiagosacro.ui.theme.VerdeClaro

// Etiqueta de color con el estado del recinto (colores del mockup)
@Composable
fun EstadoChip(estado: EstadoApertura, modifier: Modifier = Modifier) {
    val (texto, fondo, colorTexto) = when (estado) {
        EstadoApertura.ABIERTO -> Triple("Abierto", VerdeClaro, Verde)
        EstadoApertura.CERRADO -> Triple("Cerrado", SuperficieVariante, GrisCerrado)
        EstadoApertura.EN_CELEBRACION -> Triple("En celebración", OroClaro, Oro)
    }
    Surface(color = fondo, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
