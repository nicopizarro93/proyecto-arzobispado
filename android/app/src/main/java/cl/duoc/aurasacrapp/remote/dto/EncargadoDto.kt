package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.EstadoEvento
import cl.duoc.aurasacrapp.model.TipoEvento

// Datos del panel del encargado tal como viajan entre la app y el backend

// Reseña histórica del recinto (la única parte de la ficha que edita el encargado)
data class FichaRequest(
    val descripcionHistorica: String?
)

// Un bloque de horario. Las horas van como "09:00"
data class HorarioRequest(
    val diaSemana: Int,
    val horaApertura: String,
    val horaCierre: String
)

// Evento visto por el encargado: incluye el estado, para mostrar también los cancelados.
// Las fechas llegan como texto ISO, por ejemplo "2026-12-05T19:00:00"
data class EventoGestionDto(
    val id: Long,
    val tipo: TipoEvento,
    val titulo: String,
    val descripcion: String?,
    val inicio: String,
    val fin: String?,
    val estado: EstadoEvento
)

// Evento que se quiere publicar o editar
data class EventoRequest(
    val tipo: TipoEvento,
    val titulo: String,
    val descripcion: String?,
    val inicio: String,
    val fin: String?
)
