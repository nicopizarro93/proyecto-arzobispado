package cl.duoc.santiagosacro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.santiagosacro.model.Recinto
import cl.duoc.santiagosacro.model.RecintoConComuna
import kotlinx.coroutines.flow.Flow

@Dao
interface RecintoDao {

    @Insert
    suspend fun recintoAlmacenar(recinto: Recinto)

    @Insert
    suspend fun recintoAlmacenarTodos(recintos: List<Recinto>)

    @Update
    suspend fun recintoActualizar(recinto: Recinto)

    // Solo los activos: los dados de baja no se muestran en la app
    @Query("SELECT * FROM recinto WHERE activo = 1 ORDER BY nombreOficial")
    fun recintoListar(): Flow<List<Recinto>>

    @Query("SELECT * FROM recinto WHERE id = :id")
    fun recintoBuscarPorId(id: Long): Flow<Recinto?>

    // Lista de Inicio: recintos activos con el nombre de su comuna (JOIN)
    @Query(
        """
        SELECT r.id, r.tipoRecinto, r.nombreOficial, c.nombre AS comuna, r.calle, r.numeroCalle,
               r.latitud, r.longitud, r.descripcionHistorica, r.estadoApertura
        FROM recinto r
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE r.activo = 1
        ORDER BY r.nombreOficial
        """
    )
    fun recintoListarConComuna(): Flow<List<RecintoConComuna>>

    // Ficha: un recinto activo con el nombre de su comuna
    @Query(
        """
        SELECT r.id, r.tipoRecinto, r.nombreOficial, c.nombre AS comuna, r.calle, r.numeroCalle,
               r.latitud, r.longitud, r.descripcionHistorica, r.estadoApertura
        FROM recinto r
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE r.id = :id AND r.activo = 1
        """
    )
    fun recintoBuscarConComuna(id: Long): Flow<RecintoConComuna?>
}