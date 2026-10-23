package cl.duoc.santiagosacro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.santiagosacro.model.Visita
import cl.duoc.santiagosacro.model.VisitaDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitaDao {

    @Insert
    suspend fun visitaAlmacenar(visita: Visita)

    // Visitas de un usuario, de la más reciente a la más antigua
    @Query(
        """
        SELECT v.id, v.recintoId, r.nombreOficial AS nombreRecinto, r.tipoRecinto,
               c.nombre AS comuna, v.fechaHora
        FROM visita v
        INNER JOIN recinto r ON r.id = v.recintoId
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE v.usuarioId = :usuarioId
        ORDER BY v.fechaHora DESC
        """
    )
    fun visitaListarPorUsuario(usuarioId: Long): Flow<List<VisitaDetalle>>
}
