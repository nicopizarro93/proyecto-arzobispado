package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.aurasacrapp.model.Comuna
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