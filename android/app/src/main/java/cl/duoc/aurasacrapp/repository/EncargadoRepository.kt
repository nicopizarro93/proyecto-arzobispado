package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.RecintoDao
import cl.duoc.aurasacrapp.model.EventoGestion
import cl.duoc.aurasacrapp.model.FichaGestion
import cl.duoc.aurasacrapp.model.Horario
import cl.duoc.aurasacrapp.model.TipoEvento
import cl.duoc.aurasacrapp.remote.EncargadoApi
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.dto.EventoGestionDto
import cl.duoc.aurasacrapp.remote.dto.EventoRequest
import cl.duoc.aurasacrapp.remote.dto.FichaRequest
import cl.duoc.aurasacrapp.remote.dto.HorarioRequest
import cl.duoc.aurasacrapp.util.isoAMillis
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Todo lo que el encargado administra de su recinto: reseña, horarios y eventos.
// Los cambios se guardan en el backend y, si afectan lo que muestra el resto de la app,
// también en Room (que es la caché que observan las pantallas).
class EncargadoRepository(
    private val recintoDao: RecintoDao,
    private val eventoRepositorio: EventoRepository,
    private val api: EncargadoApi = RetrofitClient.encargadoApi
) {

    // ---- Ficha: reseña y horarios ----

    suspend fun ficha(): FichaGestion {
        val detalle = api.miRecintoDetalle()
        return FichaGestion(
            recintoId = detalle.id,
            historia = detalle.descripcionHistorica.orEmpty(),
            horarios = detalle.horarios.map {
                Horario(
                    id = it.id,
                    diaSemana = it.diaSemana,
                    apertura = it.horaApertura.take(5),   // "09:00:00" -> "09:00"
                    cierre = it.horaCierre.take(5)
                )
            }
        )
    }

    suspend fun guardarHistoria(recintoId: Long, texto: String) {
        val ficha = api.editarFicha(FichaRequest(texto))
        recintoDao.recintoActualizarDescripcion(recintoId, ficha.descripcionHistorica)
    }

    // horarioId null = bloque nuevo; con id = se edita ese bloque
    suspend fun guardarHorario(horarioId: Long?, dia: Int, apertura: LocalTime, cierre: LocalTime) {
        val solicitud = HorarioRequest(dia, apertura.format(HORA), cierre.format(HORA))
        if (horarioId == null) api.agregarHorario(solicitud) else api.editarHorario(horarioId, solicitud)
    }

    suspend fun eliminarHorario(horarioId: Long) {
        api.eliminarHorario(horarioId)
    }

    // ---- Eventos, avisos, aperturas y cierres ----

    suspend fun eventos(): List<EventoGestion> = api.eventos().map { it.aModelo() }

    suspend fun publicarEvento(
        tipo: TipoEvento, titulo: String, descripcion: String?, inicio: LocalDateTime, fin: LocalDateTime?
    ) {
        api.publicarEvento(solicitud(tipo, titulo, descripcion, inicio, fin))
        actualizarAgenda()
    }

    suspend fun editarEvento(
        eventoId: Long, tipo: TipoEvento, titulo: String, descripcion: String?,
        inicio: LocalDateTime, fin: LocalDateTime?
    ) {
        api.editarEvento(eventoId, solicitud(tipo, titulo, descripcion, inicio, fin))
        actualizarAgenda()
    }

    suspend fun cancelarEvento(eventoId: Long) {
        api.cancelarEvento(eventoId)
        actualizarAgenda()
    }

    // Después de cambiar un evento se vuelve a bajar la agenda para que Room (y las pantallas) se actualicen.
    // Si falla (sin conexión), el cambio ya quedó guardado en el backend.
    private suspend fun actualizarAgenda() {
        try {
            eventoRepositorio.sincronizar()
        } catch (e: Exception) {
            // la agenda se actualizará la próxima vez que se abra
        }
    }

    private fun solicitud(
        tipo: TipoEvento, titulo: String, descripcion: String?, inicio: LocalDateTime, fin: LocalDateTime?
    ) = EventoRequest(
        tipo = tipo,
        titulo = titulo.trim(),
        descripcion = descripcion?.trim()?.ifEmpty { null },
        inicio = FECHA_HORA.format(inicio),
        fin = fin?.let { FECHA_HORA.format(it) }
    )

    private fun EventoGestionDto.aModelo() = EventoGestion(
        id = id,
        tipo = tipo,
        titulo = titulo,
        descripcion = descripcion,
        inicio = isoAMillis(inicio),
        fin = fin?.let { isoAMillis(it) },
        estado = estado
    )

    private companion object {
        val HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val FECHA_HORA: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}
