package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.RecintoDao
import cl.duoc.aurasacrapp.dao.VisitaDao
import cl.duoc.aurasacrapp.model.Recinto
import cl.duoc.aurasacrapp.model.Visita
import cl.duoc.aurasacrapp.model.VisitaAgendada
import cl.duoc.aurasacrapp.model.VisitaDetalle
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.VisitaApi
import cl.duoc.aurasacrapp.remote.dto.CambioVisitaRequest
import cl.duoc.aurasacrapp.remote.dto.VisitaDto
import cl.duoc.aurasacrapp.remote.dto.VisitaRequest
import cl.duoc.aurasacrapp.util.isoAMillis
import kotlinx.coroutines.flow.Flow

// Dos tipos de visita:
// - Escaneadas: el registro local (Room) de los QR que el usuario escaneó.
// - Agendadas: las que el usuario planifica; viven en el backend.
class VisitaRepository(
    private val visitaDao: VisitaDao,
    private val recintoDao: RecintoDao,
    private val api: VisitaApi = RetrofitClient.visitaApi
) {

    // ---- Escaneadas (Room) ----

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

    // ---- Agendadas (backend) ----

    // "inicio" va en formato ISO, por ejemplo "2026-12-05T10:30:00"
    // Se agenda a un recinto o a una ruta: uno de los dos ids va null. Devuelve el id de la visita creada.
    suspend fun agendar(
        recintoId: Long?,
        rutaId: Long?,
        inicio: String,
        cantidadPersonas: Int,
        nivelCurso: String?,
        observaciones: String?
    ): Long {
        return api.agendar(
            VisitaRequest(
                idRecinto = recintoId,
                idRuta = rutaId,
                inicio = inicio,
                cantidadPersonas = cantidadPersonas,
                nivelCurso = nivelCurso,
                observaciones = observaciones
            )
        ).id
    }

    suspend fun agendadas(): List<VisitaAgendada> = api.misVisitas().map { it.aModelo() }

    // Cambia la fecha (en formato ISO), la hora y la cantidad de personas de una visita agendada
    suspend fun cambiar(visitaId: Long, inicio: String, cantidadPersonas: Int) {
        api.cambiar(visitaId, CambioVisitaRequest(inicio, cantidadPersonas))
    }

    suspend fun cancelar(visitaId: Long) {
        api.cancelar(visitaId)
    }

    private fun VisitaDto.aModelo() = VisitaAgendada(
        id = id,
        recintoId = idRecinto,
        destino = destino,
        inicio = isoAMillis(inicio),
        cantidadPersonas = cantidadPersonas,
        nivelCurso = nivelCurso,
        observaciones = observaciones,
        estado = estado
    )
}
