package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.EstadoVisita

// Datos de visitas agendadas tal como viajan entre la app y el backend

// Visita que se quiere agendar: a un recinto (idRecinto) o a una ruta (idRuta), no a las dos.
// "inicio" va como texto ISO, por ejemplo "2026-12-05T10:30:00"
data class VisitaRequest(
    val idRecinto: Long?,
    val idRuta: Long?,
    val inicio: String,
    val cantidadPersonas: Int,
    val nivelCurso: String?,
    val observaciones: String?
)

data class VisitaDto(
    val id: Long,
    val idRecinto: Long?,
    val idRuta: Long?,
    val destino: String,
    val inicio: String,
    val cantidadPersonas: Int,
    val nivelCurso: String?,
    val observaciones: String?,
    val estado: EstadoVisita
)
