package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.CambioEstadoRequest
import cl.duoc.aurasacrapp.remote.dto.RecintoDetalleDto
import cl.duoc.aurasacrapp.remote.dto.RecintoIdDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

// Endpoints del panel del encargado (exigen el token JWT de un encargado)
interface EncargadoApi {

    // Recinto que el encargado tiene asignado
    @GET("api/encargado/recinto")
    suspend fun miRecinto(): RecintoIdDto

    // Cambia el estado de apertura de su recinto
    @PATCH("api/encargado/recinto/estado")
    suspend fun cambiarEstado(@Body solicitud: CambioEstadoRequest): RecintoDetalleDto
}
