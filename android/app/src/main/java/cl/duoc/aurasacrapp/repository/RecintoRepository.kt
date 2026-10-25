package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.RecintoDao
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.RecintoConComuna
import kotlinx.coroutines.flow.Flow

// Repositorio de recintos: hoy lee de Room; en la prueba 3 podrá leer del backend
class RecintoRepository(private val recintoDao: RecintoDao) {

    // Flow: la lista se actualiza sola si cambian los datos en la BD
    val recintos: Flow<List<RecintoConComuna>> = recintoDao.recintoListarConComuna()

    fun recinto(id: Long): Flow<RecintoConComuna?> = recintoDao.recintoBuscarConComuna(id)

    // El encargado cambia el estado de su recinto: se lee, se copia con el estado nuevo y se actualiza.
    // Como la lista y la ficha usan Flow, el cambio se ve al instante en toda la app.
    suspend fun cambiarEstado(recintoId: Long, nuevoEstado: EstadoApertura) {
        val recinto = recintoDao.recintoObtenerActivo(recintoId) ?: return
        recintoDao.recintoActualizar(recinto.copy(estadoApertura = nuevoEstado))
    }
}
