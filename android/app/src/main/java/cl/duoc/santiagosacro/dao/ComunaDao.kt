package cl.duoc.santiagosacro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.santiagosacro.model.Comuna
import kotlinx.coroutines.flow.Flow

@Dao
interface ComunaDao {

    @Insert
    suspend fun comunaAlmacenarTodas(comunas: List<Comuna>)

    @Query("SELECT * FROM comuna ORDER BY nombre")
    fun comunaListar(): Flow<List<Comuna>>

    // Sirve para saber si la BD está vacía
    @Query("SELECT COUNT(*) FROM comuna")
    suspend fun comunaContar(): Int
}