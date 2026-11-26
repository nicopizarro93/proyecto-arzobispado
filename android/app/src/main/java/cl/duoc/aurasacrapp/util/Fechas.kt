package cl.duoc.aurasacrapp.util

import java.time.LocalDateTime
import java.time.ZoneId

// Convierte una fecha ISO del backend ("2026-12-05T19:00:00") en milisegundos,
// que es como Room guarda las fechas
fun isoAMillis(texto: String): Long =
    LocalDateTime.parse(texto).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
