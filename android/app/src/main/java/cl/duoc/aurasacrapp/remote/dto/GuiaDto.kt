package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.EstadoSolicitud

// Guías y solicitudes de guía tal como viajan entre la app y el backend.
// Las fechas van como texto ISO, por ejemplo "2026-12-05T10:00:00"

data class GuiaRequest(
    val nombre: String,
    val apellido: String
)

data class GuiaActivoRequest(
    val activo: Boolean
)

data class DisponibilidadRequest(
    val inicio: String,
    val fin: String
)

data class DisponibilidadDto(
    val id: Long,
    val inicio: String,
    val fin: String
)

data class GuiaDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val activo: Boolean,
    val disponibilidad: List<DisponibilidadDto>
)

data class AsignarGuiaRequest(
    val idGuia: Long
)

data class RechazarSolicitudRequest(
    val motivo: String
)

data class SolicitudRecintoDto(
    val id: Long,
    val estado: EstadoSolicitud,
    val idVisita: Long,
    val inicio: String,
    val cantidadPersonas: Int,
    val nivelCurso: String?,
    val solicitante: String,
    val institucion: String?,
    val guia: String?,
    val motivoRechazo: String?
)

// El recinto de la visita para el que se pide un guía
data class SolicitudGuiaRequest(
    val idRecinto: Long
)

data class SolicitudUsuarioDto(
    val id: Long,
    val idVisita: Long,
    val idRecinto: Long,
    val recinto: String,
    val estado: EstadoSolicitud,
    val guia: String?,
    val motivoRechazo: String?
)
