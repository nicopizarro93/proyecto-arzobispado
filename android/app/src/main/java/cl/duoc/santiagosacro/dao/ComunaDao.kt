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
}