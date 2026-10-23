package cl.duoc.santiagosacro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.santiagosacro.model.Evento
import cl.duoc.santiagosacro.model.EventoDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Insert
    suspend fun eventoAlmacenarTodos(eventos: List<Evento>)

    // Agenda: eventos publicados que aún no terminan, del más próximo al más lejano.
    // COALESCE: si no tiene hora de término, se usa la de inicio.
    @Query(
        """
        SELECT e.id, e.recintoId, r.nombreOficial AS nombreRecinto, c.nombre AS comuna,
               e.tipoEvento, e.titulo, e.descripcion, e.fechaHoraInicio, e.fechaHoraFin, e.estado
        FROM evento e
        INNER JOIN recinto r ON r.id = e.recintoId
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE e.estado = 'PUBLICADO' AND r.activo = 1
          AND COALESCE(e.fechaHoraFin, e.fechaHoraInicio) >= :desde
        ORDER BY e.fechaHoraInicio
        """
    )
    fun eventoListarProximos(desde: Long): Flow<List<EventoDetalle>>

    // Ficha: los próximos eventos de un recinto
    @Query(
        """
        SELECT e.id, e.recintoId, r.nombreOficial AS nombreRecinto, c.nombre AS comuna,
               e.tipoEvento, e.titulo, e.descripcion, e.fechaHoraInicio, e.fechaHoraFin, e.estado
        FROM evento e
        INNER JOIN recinto r ON r.id = e.recintoId
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE e.recintoId = :recintoId AND e.estado = 'PUBLICADO'
          AND COALESCE(e.fechaHoraFin, e.fechaHoraInicio) >= :desde
        ORDER BY e.fechaHoraInicio
        """
    )
    fun eventoListarProximosPorRecinto(recintoId: Long, desde: Long): Flow<List<EventoDetalle>>

    // Detalle de un evento
    @Query(
        """
        SELECT e.id, e.recintoId, r.nombreOficial AS nombreRecinto, c.nombre AS comuna,
               e.tipoEvento, e.titulo, e.descripcion, e.fechaHoraInicio, e.fechaHoraFin, e.estado
        FROM evento e
        INNER JOIN recinto r ON r.id = e.recintoId
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE e.id = :id
        """
    )
    fun eventoBuscarPorId(id: Long): Flow<EventoDetalle?>
}
