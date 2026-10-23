package cl.duoc.santiagosacro.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.santiagosacro.ui.theme.Oro
import cl.duoc.santiagosacro.ui.theme.SantiagoSacroTheme
import cl.duoc.santiagosacro.ui.theme.Terracota
import cl.duoc.santiagosacro.ui.theme.TerracotaClaro
import cl.duoc.santiagosacro.ui.theme.TerracotaOscuro
import kotlin.math.cos
import kotlin.math.sin

// Pantalla de bienvenida (igual al mockup): rosetón, nombre, eslogan y botón de ingreso
@Composable
fun BienvenidaScreen(onIniciarSesion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Roseton(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Santiago Sacro",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "Templos, arte e historia de Santiago",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 19.sp
                ),
                color = TerracotaOscuro
            )
            Text(
                text = "Descubre templos, museos, colegios y archivos de Santiago: " +
                    "conoce su historia y registra tus visitas escaneando el código QR de cada lugar.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onIniciarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = "Iniciar sesión", fontSize = 16.sp)
            }
        }
    }
}

// Dibujo del rosetón (vitral circular) hecho con círculos en un Canvas.
// Las medidas son las del SVG del mockup (390 x 360) escaladas al tamaño real.
@Composable
private fun Roseton(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = minOf(size.width / 390f, size.height / 360f)   // factor de escala
        val centro = Offset(size.width / 2f, size.height / 2f)

        // Dos anillos exteriores dorados
        drawCircle(color = Oro, radius = 150f * s, center = centro, alpha = 0.35f, style = Stroke(width = 1.5f * s))
        drawCircle(color = Oro, radius = 138f * s, center = centro, alpha = 0.25f, style = Stroke(width = 1f * s))

        for (i in 0 until 12) {
            // 12 círculos terracota que se cruzan (los "pétalos")
            val angulo = Math.toRadians(i * 30.0)
            val centroPetalo = Offset(
                centro.x + 69f * s * cos(angulo).toFloat(),
                centro.y + 69f * s * sin(angulo).toFloat()
            )
            drawCircle(color = Terracota, radius = 69f * s, center = centroPetalo, alpha = 0.55f, style = Stroke(width = 1.4f * s))

            // 12 puntos dorados entre los pétalos
            val anguloPunto = Math.toRadians(i * 30.0 + 15.0)
            val punto = Offset(
                centro.x + 146f * s * cos(anguloPunto).toFloat(),
                centro.y + 146f * s * sin(anguloPunto).toFloat()
            )
            drawCircle(color = Oro, radius = 4f * s, center = punto, alpha = 0.55f)
        }

        // Centro del rosetón
        drawCircle(color = TerracotaClaro, radius = 34f * s, center = centro)
        drawCircle(color = Terracota, radius = 34f * s, center = centro, style = Stroke(width = 1.6f * s))
        drawCircle(color = Terracota, radius = 12f * s, center = centro)
    }
}

@Preview(showBackground = true)
@Composable
private fun BienvenidaPreview() {
    SantiagoSacroTheme {
        BienvenidaScreen(onIniciarSesion = {})
    }
}
