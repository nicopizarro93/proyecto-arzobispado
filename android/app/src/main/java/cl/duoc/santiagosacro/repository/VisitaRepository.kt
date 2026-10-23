package cl.duoc.santiagosacro.repository

import cl.duoc.santiagosacro.dao.RecintoDao
import cl.duoc.santiagosacro.dao.VisitaDao
import cl.duoc.santiagosacro.model.Recinto
import cl.duoc.santiagosacro.model.Visita
import cl.duoc.santiagosacro.model.VisitaDetalle
import kotlinx.coroutines.flow.Flow

class VisitaRepository(
    private val visitaDao: VisitaDao,
    private val recintoDao: RecintoDao
) {

    fun visitasDe(usuarioId: Long): Flow<List<VisitaDetalle>> =
        visitaDao.visitaListarPorUsuario(usuarioId)

    // Registra la visita solo si el recinto existe y está activo.
    // Devuelve el recinto visitado, o null si el código no corresponde a ningún recinto.
    suspend fun registrarVisita(usuarioId: Long, recintoId: Long): Recinto? {
        val recinto = recintoDao.recintoObtenerActivo(recintoId) ?: return null
        visitaDao.visitaAlmacenar(
            Visita(usuarioId = usuarioId, recintoId = recinto.id, fechaHora = System.currentTimeMillis())
        )
        return recinto
    }
}
