package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.RutaDetalleDto
import cl.duoc.aurasacrapp.remote.dto.RutaRequest
import cl.duoc.aurasacrapp.remote.dto.RutaResumenDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Rutas sugeridas (públicas) y rutas propias del usuario (exigen el token)
interface RutaApi {

    @GET("api/rutas")
    suspend fun rutas(): List<RutaResumenDto>

    @GET("api/rutas/{id}")
    suspend fun ruta(@Path("id") id: Long): RutaDetalleDto

    @GET("api/rutas/mias")
    suspend fun misRutas(): List<RutaResumenDto>

    @GET("api/rutas/mias/{id}")
    suspend fun miRuta(@Path("id") id: Long): RutaDetalleDto

    @POST("api/rutas/mias")
    suspend fun crearRuta(@Body solicitud: RutaRequest): RutaDetalleDto
}
