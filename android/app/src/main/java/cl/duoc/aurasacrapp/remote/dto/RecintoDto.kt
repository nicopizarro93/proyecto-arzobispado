package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.TipoRecinto

// Datos de recintos tal como los entrega el backend (se ignoran los campos que la app no usa)

data class ComunaDto(
    val id: Long,
    val nombre: String
)

// Elemento de la lista de recintos
data class RecintoResumenDto(
    val id: Long,
    val nombre: String,
    val tipo: TipoRecinto,
    val comuna: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val estado: EstadoApertura
)

// Un bloque de horario. Las horas llegan como "09:00:00"
data class HorarioDto(
    val diaSemana: Int,
    val horaApertura: String,
    val horaCierre: String
)

// Ficha completa de un recinto
data class RecintoDetalleDto(
    val id: Long,
    val descripcionHistorica: String?,
    val estado: EstadoApertura,
    val horarios: List<HorarioDto>
)

// Cuerpo para cambiar el estado de apertura del recinto (solo encargados)
data class CambioEstadoRequest(
    val estado: EstadoApertura
)
