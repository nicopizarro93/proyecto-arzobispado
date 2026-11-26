package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.TipoEvento

// Las fechas llegan como texto ISO, por ejemplo "2026-12-05T19:00:00"

// Elemento de la lista de eventos
data class EventoResumenDto(
    val id: Long,
    val tipo: TipoEvento,
    val titulo: String,
    val inicio: String,
    val fin: String?,
    val idRecinto: Long
)

// Detalle de un evento (incluye la descripción)
data class EventoDetalleDto(
    val id: Long,
    val descripcion: String?
)
