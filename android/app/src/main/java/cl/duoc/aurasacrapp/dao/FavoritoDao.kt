package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import cl.duoc.aurasacrapp.model.Favorito
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    // Marca un recinto como favorito (si ya lo era, no pasa nada)
    @Upsert
    suspend fun favoritoAgregar(favorito: Favorito)

    @Query("DELETE FROM favorito WHERE usuarioId = :usuarioId AND recintoId = :recintoId")
    suspend fun favoritoQuitar(usuarioId: Long, recintoId: Long)

    // Consulta única: ¿este recinto ya es favorito del usuario?
    @Query("SELECT COUNT(*) FROM favorito WHERE usuarioId = :usuarioId AND recintoId = :recintoId")
    suspend fun favoritoContar(usuarioId: Long, recintoId: Long): Int

    // Los ids de los recintos favoritos del usuario; la pantalla se actualiza sola cuando cambian
    @Query("SELECT recintoId FROM favorito WHERE usuarioId = :usuarioId")
    fun favoritoIds(usuarioId: Long): Flow<List<Long>>
}
