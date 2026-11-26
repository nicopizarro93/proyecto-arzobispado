package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.ComunaDto
import cl.duoc.aurasacrapp.remote.dto.RecintoDetalleDto
import cl.duoc.aurasacrapp.remote.dto.RecintoResumenDto
import retrofit2.http.GET
import retrofit2.http.Path

// Consulta de recintos y comunas (endpoints públicos)
interface RecintoApi {

    @GET("api/comunas")
    suspend fun comunas(): List<ComunaDto>

    @GET("api/recintos")
    suspend fun recintos(): List<RecintoResumenDto>

    @GET("api/recintos/{id}")
    suspend fun recinto(@Path("id") id: Long): RecintoDetalleDto
}
