package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Upsert
import androidx.room3.Query
import cl.duoc.aurasacrapp.model.Comuna
import kotlinx.coroutines.flow.Flow

@Dao
interface ComunaDao {

    // Inserta las comunas nuevas y actualiza las que ya existen
    @Upsert
    suspend fun comunaGuardarTodas(comunas: List<Comuna>)

    @Query("SELECT * FROM comuna ORDER BY nombre")
    fun comunaListar(): Flow<List<Comuna>>

}