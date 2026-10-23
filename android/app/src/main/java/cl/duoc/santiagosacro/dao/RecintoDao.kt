package cl.duoc.santiagosacro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.santiagosacro.model.Recinto
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
}