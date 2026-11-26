package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.ModoTraslado

// Datos de rutas tal como los entrega el backend

// Elemento de la lista de rutas
data class RutaResumenDto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val modo: ModoTraslado,
    val cantidadParadas: Int,
    val minutosTotal: Int
)

data class ParadaDto(
    val orden: Int,
    val idRecinto: Long,
    val recinto: String,
    val estado: EstadoApertura,
    val latitud: Double,
    val longitud: Double,
    val tiempoVisitaMin: Int
)

// Traslado entre dos paradas seguidas
data class TramoDto(
    val minutos: Int
)

// Tiempo estimado que calcula el backend (visitas + traslados)
data class EstimacionDto(
    val minutosVisita: Int,
    val minutosTraslado: Int,
    val minutosTotal: Int,
    val tramos: List<TramoDto>
)

data class RutaDetalleDto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val modo: ModoTraslado,
    val paradas: List<ParadaDto>,
    val estimacion: EstimacionDto
)

// Una parada de la ruta que se va a guardar (si no se indican los minutos, el backend usa 30)
data class ParadaRequest(
    val idRecinto: Long,
    val tiempoVisitaMin: Int? = null
)

data class RutaRequest(
    val nombre: String,
    val descripcion: String?,
    val modo: ModoTraslado,
    val paradas: List<ParadaRequest>
)
