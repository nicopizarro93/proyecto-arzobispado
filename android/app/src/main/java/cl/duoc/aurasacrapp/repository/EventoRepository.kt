package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.EventoDao
import cl.duoc.aurasacrapp.model.EventoDetalle
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow

class EventoRepository(private val eventoDao: EventoDao) {

    fun proximos(): Flow<List<EventoDetalle>> = eventoDao.eventoListarProximos(inicioDeHoy())

    fun proximosDe(recintoId: Long): Flow<List<EventoDetalle>> =
        eventoDao.eventoListarProximosPorRecinto(recintoId, inicioDeHoy())

    fun evento(id: Long): Flow<EventoDetalle?> = eventoDao.eventoBuscarPorId(id)

    // Hoy a las 00:00, en milisegundos: así los eventos de hoy siguen apareciendo
    private fun inicioDeHoy(): Long =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
