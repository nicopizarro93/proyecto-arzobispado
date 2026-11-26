package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.EventoDao
import cl.duoc.aurasacrapp.model.Evento
import cl.duoc.aurasacrapp.model.EventoDetalle
import cl.duoc.aurasacrapp.remote.EventoApi
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.util.isoAMillis
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

// Repositorio de eventos: los datos vienen del backend y se guardan en Room (caché)
class EventoRepository(
    private val eventoDao: EventoDao,
    private val api: EventoApi = RetrofitClient.eventoApi
) {

    fun proximos(): Flow<List<EventoDetalle>> = eventoDao.eventoListarProximos(inicioDeHoy())

    fun proximosDe(recintoId: Long): Flow<List<EventoDetalle>> =
        eventoDao.eventoListarProximosPorRecinto(recintoId, inicioDeHoy())

    fun evento(id: Long): Flow<EventoDetalle?> = eventoDao.eventoBuscarPorId(id)

    // Baja los eventos del backend y los guarda en Room.
    // Hay que sincronizar los recintos antes: cada evento apunta a su recinto.
    suspend fun sincronizar() {
        val remotos = api.eventos()
        // La lista no trae la descripción: se conserva la que ya se había descargado
        val descripcionPrevia = eventoDao.eventoListarTodos().associate { it.id to it.descripcion }
        val eventos = remotos.map { e ->
            Evento(
                id = e.id,
                recintoId = e.idRecinto,
                tipoEvento = e.tipo,
                titulo = e.titulo,
                descripcion = descripcionPrevia[e.id],
                fechaHoraInicio = isoAMillis(e.inicio),
                fechaHoraFin = e.fin?.let { isoAMillis(it) }
            )
        }
        eventoDao.eventoGuardarTodos(eventos)
        eventoDao.eventoBorrarExcepto(eventos.map { it.id })
    }

    // Detalle de un evento: guarda su descripción en Room
    suspend fun cargarDetalle(id: Long) {
        val detalle = api.evento(id)
        eventoDao.eventoActualizarDescripcion(id, detalle.descripcion)
    }

    // Hoy a las 00:00, en milisegundos: así los eventos de hoy siguen apareciendo
    private fun inicioDeHoy(): Long =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
