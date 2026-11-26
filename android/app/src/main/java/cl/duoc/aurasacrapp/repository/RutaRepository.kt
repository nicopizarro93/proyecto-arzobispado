package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.model.ParadaRuta
import cl.duoc.aurasacrapp.model.Ruta
import cl.duoc.aurasacrapp.model.RutaDetalle
import cl.duoc.aurasacrapp.model.RutaResumen
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.RutaApi
import cl.duoc.aurasacrapp.remote.dto.ParadaRequest
import cl.duoc.aurasacrapp.remote.dto.RutaDetalleDto
import cl.duoc.aurasacrapp.remote.dto.RutaRequest
import cl.duoc.aurasacrapp.remote.dto.RutaResumenDto
import retrofit2.HttpException

// Rutas sugeridas por la red y rutas propias del usuario. Se piden al backend,
// que también calcula el tiempo estimado (visitas + traslados con Haversine).
class RutaRepository(private val api: RutaApi = RetrofitClient.rutaApi) {

    suspend fun sugeridas(): List<RutaResumen> = api.rutas().map { it.aModelo() }

    suspend fun mias(): List<RutaResumen> = api.misRutas().map { it.aModelo() }

    // Una ruta puede ser sugerida o propia: se prueba primero como sugerida y,
    // si el backend responde 404, como una de mis rutas
    suspend fun detalle(id: Long): RutaDetalle {
        val dto = try {
            api.ruta(id)
        } catch (e: HttpException) {
            if (e.code() == 404) api.miRuta(id) else throw e
        }
        return dto.aModelo()
    }

    // Guarda una ruta propia; las paradas van en el orden en que se eligieron
    suspend fun crear(nombre: String, modo: ModoTraslado, recintoIds: List<Long>): RutaDetalle {
        val solicitud = RutaRequest(
            nombre = nombre.trim(),
            descripcion = null,
            modo = modo,
            paradas = recintoIds.map { ParadaRequest(idRecinto = it) }
        )
        return api.crearRuta(solicitud).aModelo()
    }

    private fun RutaResumenDto.aModelo() = RutaResumen(
        id = id,
        nombre = nombre,
        modoTraslado = modo,
        cantidadParadas = cantidadParadas,
        minutosTotales = minutosTotal
    )

    private fun RutaDetalleDto.aModelo() = RutaDetalle(
        ruta = Ruta(
            id = id,
            nombre = nombre,
            descripcion = descripcion.orEmpty(),
            modoTraslado = modo,
            paradas = paradas.map {
                ParadaRuta(
                    orden = it.orden,
                    recintoId = it.idRecinto,
                    nombreRecinto = it.recinto,
                    estado = it.estado,
                    latitud = it.latitud,
                    longitud = it.longitud,
                    tiempoVisitaMin = it.tiempoVisitaMin
                )
            }
        ),
        traslados = estimacion.tramos.map { it.minutos },
        minutosVisita = estimacion.minutosVisita,
        minutosTraslado = estimacion.minutosTraslado,
        minutosTotales = estimacion.minutosTotal
    )
}
