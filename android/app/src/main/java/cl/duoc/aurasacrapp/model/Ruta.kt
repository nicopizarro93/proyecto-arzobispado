package cl.duoc.aurasacrapp.model

// Ruta patrimonial con sus paradas en orden.
// No es una tabla de Room: los datos se piden al backend (RutaRepository).
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
    val estado: EstadoApertura,      // para avisar si el recinto está cerrado
    val latitud: Double,
    val longitud: Double,
    val tiempoVisitaMin: Int
)

// Ruta para las listas: solo los datos que se muestran
data class RutaResumen(
    val id: Long,
    val nombre: String,
    val modoTraslado: ModoTraslado,
    val cantidadParadas: Int,
    val minutosTotales: Int
)

// Detalle de una ruta: paradas, minutos de cada traslado y totales (los calcula el backend)
data class RutaDetalle(
    val ruta: Ruta,
    val traslados: List<Int>,
    val minutosVisita: Int,
    val minutosTraslado: Int,
    val minutosTotales: Int
)
