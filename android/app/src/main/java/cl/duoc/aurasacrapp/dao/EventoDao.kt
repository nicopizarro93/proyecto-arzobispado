package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import cl.duoc.aurasacrapp.model.Evento
import cl.duoc.aurasacrapp.model.EventoDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    // Sincronización con el backend: inserta los eventos nuevos y actualiza los que ya existen
    @Upsert
    suspend fun eventoGuardarTodos(eventos: List<Evento>)

    // Quita los eventos que el backend ya no entrega (cancelados o terminados)
    @Query("DELETE FROM evento WHERE id NOT IN (:ids)")
    suspend fun eventoBorrarExcepto(ids: List<Long>)

    // Copia de todos los eventos guardados (para conservar la descripción al sincronizar)
    @Query("SELECT * FROM evento")
    suspend fun eventoListarTodos(): List<Evento>

    @Query("UPDATE evento SET descripcion = :descripcion WHERE id = :id")
    suspend fun eventoActualizarDescripcion(id: Long, descripcion: String?)

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
