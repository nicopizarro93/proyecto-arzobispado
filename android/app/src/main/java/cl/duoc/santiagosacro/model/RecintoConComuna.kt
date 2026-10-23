package cl.duoc.santiagosacro.model

// Recinto junto con el nombre de su comuna.
// No es una tabla: es el resultado de una consulta con JOIN (recinto + comuna).
data class RecintoConComuna(
    val id: Long,
    val tipoRecinto: TipoRecinto,
    val nombreOficial: String,
    val comuna: String,
    val calle: String,
    val numeroCalle: String?,
    val latitud: Double,
    val longitud: Double,
    val descripcionHistorica: String?,
    val estadoApertura: EstadoApertura
)
