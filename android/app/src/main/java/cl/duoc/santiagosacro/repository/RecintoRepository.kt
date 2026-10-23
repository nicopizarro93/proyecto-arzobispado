package cl.duoc.santiagosacro.repository

import cl.duoc.santiagosacro.dao.RecintoDao
import cl.duoc.santiagosacro.model.RecintoConComuna
import kotlinx.coroutines.flow.Flow

// Repositorio de recintos: hoy lee de Room; en la prueba 3 podrá leer del backend
class RecintoRepository(private val recintoDao: RecintoDao) {

    // Flow: la lista se actualiza sola si cambian los datos en la BD
    val recintos: Flow<List<RecintoConComuna>> = recintoDao.recintoListarConComuna()

    fun recinto(id: Long): Flow<RecintoConComuna?> = recintoDao.recintoBuscarConComuna(id)
}
