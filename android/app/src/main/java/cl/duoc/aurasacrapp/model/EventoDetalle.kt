package cl.duoc.aurasacrapp.model

// Evento con el nombre de su recinto y comuna.
// No es una tabla: es el resultado de una consulta con JOIN (evento + recinto + comuna).
data class EventoDetalle(
    val id: Long,
    val recintoId: Long,
    val nombreRecinto: String,
    val comuna: String,
    val tipoEvento: TipoEvento,
    val titulo: String,
    val descripcion: String?,
    val fechaHoraInicio: Long,
    val fechaHoraFin: Long?,
    val estado: EstadoEvento
)
