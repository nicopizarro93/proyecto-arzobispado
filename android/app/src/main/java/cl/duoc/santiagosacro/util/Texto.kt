package cl.duoc.santiagosacro.util

import java.text.Normalizer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Quita tildes y diéresis para comparar textos: "Maipú" -> "Maipu", "Ñuñoa" -> "Nunoa".
// NFD separa cada letra de su tilde (ú = u + ´) y la expresión regular borra las tildes.
fun String.sinAcentos(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

// Fecha y hora legible en español de Chile: "23 oct 2026 · 18:45"
private val formatoFecha = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.forLanguageTag("es-CL"))

fun formatearFecha(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatoFecha)

private val espanolChile = Locale.forLanguageTag("es-CL")

private fun aFecha(millis: Long) = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())

// Nombre del día: 1 = "Lunes" ... 7 = "Domingo"
fun nombreDia(diaSemana: Int): String =
    listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")[diaSemana - 1]

// Día de la semana de hoy: 1 = lunes ... 7 = domingo
fun diaDeHoy(): Int = java.time.LocalDate.now().dayOfWeek.value

// "45 min" o "2 h 15 min"
fun formatearDuracion(minutos: Int): String {
    if (minutos < 60) return "$minutos min"
    val horas = minutos / 60
    val resto = minutos % 60
    return if (resto == 0) "$horas h" else "$horas h $resto min"
}

// Para la etiqueta de fecha de los eventos: "28" y "OCT"
fun diaDelMes(millis: Long): String = aFecha(millis).dayOfMonth.toString()

fun mesCorto(millis: Long): String =
    aFecha(millis).format(DateTimeFormatter.ofPattern("MMM", espanolChile)).replace(".", "").uppercase()

fun formatearHora(millis: Long): String = aFecha(millis).format(DateTimeFormatter.ofPattern("HH:mm"))

// "Jueves 29 de octubre · 19:00 a 20:30", o "Del 25 oct al 2 dic" si dura varios días
fun formatearRango(inicio: Long, fin: Long?): String {
    val desde = aFecha(inicio)
    val dia = desde.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", espanolChile))
        .replaceFirstChar { it.uppercase() }
    if (fin == null) return "$dia · ${formatearHora(inicio)}"
    val hasta = aFecha(fin)
    if (desde.toLocalDate() == hasta.toLocalDate()) {
        return "$dia · ${formatearHora(inicio)} a ${formatearHora(fin)}"
    }
    val corto = DateTimeFormatter.ofPattern("d MMM", espanolChile)
    return "Del ${desde.format(corto).replace(".", "")} al ${hasta.format(corto).replace(".", "")}"
}
