package cl.duoc.aurasacrapp.view.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import cl.duoc.aurasacrapp.model.Horario
import cl.duoc.aurasacrapp.util.diaDeHoy
import cl.duoc.aurasacrapp.util.nombreDia

// Horario de la semana: una línea por día, con el día de hoy en negrita
@Composable
fun HorarioSemana(horarios: List<Horario>) {
    if (horarios.isEmpty()) {
        Text("Este recinto aún no informa su horario.")
        return
    }
    Column {
        (1..7).forEach { dia ->
            val bloques = horarios.filter { it.diaSemana == dia }
            val texto = if (bloques.isEmpty()) "Cerrado" else bloques.joinToString(" y ") { "${it.apertura}–${it.cierre}" }
            Text(
                text = "${nombreDia(dia)}: $texto",
                fontWeight = if (dia == diaDeHoy()) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
