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
