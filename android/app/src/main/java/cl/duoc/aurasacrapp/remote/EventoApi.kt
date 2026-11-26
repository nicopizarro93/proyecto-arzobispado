package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.EventoDetalleDto
import cl.duoc.aurasacrapp.remote.dto.EventoResumenDto
import retrofit2.http.GET
import retrofit2.http.Path

// Consulta de eventos culturales (endpoints públicos)
interface EventoApi {

    @GET("api/eventos")
    suspend fun eventos(): List<EventoResumenDto>

    @GET("api/eventos/{id}")
    suspend fun evento(@Path("id") id: Long): EventoDetalleDto
}
