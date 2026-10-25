package cl.duoc.aurasacrapp.view.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.aurasacrapp.model.Horario
import cl.duoc.aurasacrapp.util.diaDeHoy
import cl.duoc.aurasacrapp.util.nombreDia

// Horario de la semana: un día por fila y el día de hoy destacado
@Composable
fun HorarioSemana(horarios: List<Horario>, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        if (horarios.isEmpty()) {
            Text(
                text = "Este recinto aún no informa su horario.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val hoy = diaDeHoy()
                (1..7).forEach { dia ->
                    val bloques = horarios.filter { it.diaSemana == dia }
                    val esHoy = dia == hoy
                    val peso = if (esHoy) FontWeight.Bold else FontWeight.Normal
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (esHoy) "${nombreDia(dia)} (hoy)" else nombreDia(dia),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = peso),
                            color = if (esHoy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (bloques.isEmpty()) {
                                "Cerrado"
                            } else {
                                bloques.joinToString(" · ") { "${it.apertura}–${it.cierre}" }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = peso),
                            color = if (bloques.isEmpty()) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}
