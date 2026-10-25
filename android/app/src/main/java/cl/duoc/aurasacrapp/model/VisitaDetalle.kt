package cl.duoc.aurasacrapp.model

// Visita con los datos del recinto para mostrar en "Mis visitas".
// No es una tabla: es el resultado de una consulta con JOIN (visita + recinto + comuna).
data class VisitaDetalle(
    val id: Long,
    val recintoId: Long,
    val nombreRecinto: String,
    val tipoRecinto: TipoRecinto,
    val comuna: String,
    val fechaHora: Long
)
