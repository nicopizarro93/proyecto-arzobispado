package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.FavoritoDao
import cl.duoc.aurasacrapp.model.Favorito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Favoritos del usuario: viven solo en el teléfono (Room), no en el backend
class FavoritoRepository(private val favoritoDao: FavoritoDao) {

    // Ids de los recintos favoritos. Flow: se actualiza solo cuando se marca o desmarca uno
    fun ids(usuarioId: Long): Flow<Set<Long>> = favoritoDao.favoritoIds(usuarioId).map { it.toSet() }

    // Marca el recinto si no era favorito, y lo desmarca si ya lo era
    suspend fun alternar(usuarioId: Long, recintoId: Long) {
        if (favoritoDao.favoritoContar(usuarioId, recintoId) > 0) {
            favoritoDao.favoritoQuitar(usuarioId, recintoId)
        } else {
            favoritoDao.favoritoAgregar(Favorito(usuarioId = usuarioId, recintoId = recintoId))
        }
    }
}
