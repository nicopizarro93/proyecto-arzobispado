package cl.duoc.santiagosacro.model

// Ruta patrimonial sugerida, con sus paradas en orden.
// No es una tabla de Room: en esta entrega los datos son fijos (RutaRepository)
// y en la prueba 3 vendrán del backend.
data class Ruta(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val modoTraslado: ModoTraslado,
    val paradas: List<ParadaRuta>
)

data class ParadaRuta(
    val orden: Int,
    val recintoId: Long,
    val nombreRecinto: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val tiempoVisitaMin: Int
)
