package cl.duoc.aurasacrapp.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Convierte una fecha ISO del backend ("2026-12-05T19:00:00") en milisegundos,
// que es como Room guarda las fechas
fun isoAMillis(texto: String): Long =
    LocalDateTime.parse(texto).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

private val FORMATO_FECHA_TEXTO = DateTimeFormatter.ofPattern("d/M/uuuu")
private val FORMATO_HORA_TEXTO = DateTimeFormatter.ofPattern("H:mm")

// Lee "dd/mm/aaaa" (null si no es una fecha válida)
fun parsearFecha(texto: String): LocalDate? = try {
    LocalDate.parse(texto.trim(), FORMATO_FECHA_TEXTO)
} catch (e: DateTimeParseException) {
    null
}

// Lee "hh:mm" (null si no es una hora válida)
fun parsearHora(texto: String): LocalTime? = try {
    LocalTime.parse(texto.trim(), FORMATO_HORA_TEXTO)
} catch (e: DateTimeParseException) {
    null
}

// Para rellenar los formularios al editar: milisegundos -> "dd/mm/aaaa" y "hh:mm"
fun millisAFechaTexto(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"))

fun millisAHoraTexto(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"))
