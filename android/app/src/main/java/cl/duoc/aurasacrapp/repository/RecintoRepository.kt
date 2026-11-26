package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.ComunaDao
import cl.duoc.aurasacrapp.dao.RecintoDao
import cl.duoc.aurasacrapp.model.Comuna
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.Horario
import cl.duoc.aurasacrapp.model.Recinto
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.remote.EncargadoApi
import cl.duoc.aurasacrapp.remote.RecintoApi
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.dto.CambioEstadoRequest
import kotlinx.coroutines.flow.Flow

// Repositorio de recintos: los datos vienen del backend y se guardan en Room (caché).
// Las pantallas observan Room con Flow: cuando la sincronización guarda datos nuevos,
// se actualizan solas, y sin internet siguen mostrando los últimos datos guardados.
class RecintoRepository(
    private val recintoDao: RecintoDao,
    private val comunaDao: ComunaDao,
    private val api: RecintoApi = RetrofitClient.recintoApi,
    private val encargadoApi: EncargadoApi = RetrofitClient.encargadoApi
) {

    // Flow: la lista se actualiza sola si cambian los datos en la BD
    val recintos: Flow<List<RecintoConComuna>> = recintoDao.recintoListarConComuna()

    fun recinto(id: Long): Flow<RecintoConComuna?> = recintoDao.recintoBuscarConComuna(id)

    // Baja las comunas y los recintos del backend y los guarda en Room
    suspend fun sincronizar() {
        val comunas = api.comunas()
        val remotos = api.recintos()
        val comunaPorNombre = comunas.associate { it.nombre to it.id }
        // La lista no trae la historia: se conserva la que ya se había descargado
        val historiaPrevia = recintoDao.recintoListarTodos().associate { it.id to it.descripcionHistorica }

        comunaDao.comunaGuardarTodas(comunas.map { Comuna(id = it.id, nombre = it.nombre) })
        val recintos = remotos.mapNotNull { r ->
            val comunaId = comunaPorNombre[r.comuna] ?: return@mapNotNull null
            Recinto(
                id = r.id,
                tipoRecinto = r.tipo,
                comunaId = comunaId,
                nombreOficial = r.nombre,
                direccion = r.direccion,
                latitud = r.latitud,
                longitud = r.longitud,
                descripcionHistorica = historiaPrevia[r.id],
                estadoApertura = r.estado,
                activo = true
            )
        }
        recintoDao.recintoGuardarTodos(recintos)
        recintoDao.recintoDesactivarExcepto(recintos.map { it.id })
    }

    // Ficha de un recinto: guarda su historia en Room y devuelve el horario de la semana
    suspend fun cargarFicha(id: Long): List<Horario> {
        val detalle = api.recinto(id)
        recintoDao.recintoActualizarDescripcion(id, detalle.descripcionHistorica)
        return detalle.horarios.map {
            Horario(
                id = it.id,
                diaSemana = it.diaSemana,
                apertura = it.horaApertura.take(5),   // "09:00:00" -> "09:00"
                cierre = it.horaCierre.take(5)
            )
        }
    }

    // El encargado cambia el estado de su recinto: se guarda en el backend y, si sale bien,
    // también en Room. Como la lista y la ficha usan Flow, el cambio se ve al instante en toda la app.
    suspend fun cambiarEstado(recintoId: Long, nuevoEstado: EstadoApertura) {
        encargadoApi.cambiarEstado(CambioEstadoRequest(nuevoEstado))
        val recinto = recintoDao.recintoObtenerActivo(recintoId) ?: return
        recintoDao.recintoActualizar(recinto.copy(estadoApertura = nuevoEstado))
    }
}
