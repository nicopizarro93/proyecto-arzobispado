package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.CuentaActivaRequest
import cl.duoc.aurasacrapp.remote.dto.EncargadoDto
import cl.duoc.aurasacrapp.remote.dto.EncargadoNuevoRequest
import cl.duoc.aurasacrapp.remote.dto.RecintoDetalleDto
import cl.duoc.aurasacrapp.remote.dto.RecintoNuevoRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Panel del administrador de la red (exige el token JWT de un administrador)
interface AdminApi {

    // HU-35: registrar un recinto en la red
    @POST("api/admin/recintos")
    suspend fun registrarRecinto(@Body solicitud: RecintoNuevoRequest): RecintoDetalleDto

    // HU-35: baja lógica; responde 204 sin contenido
    @PATCH("api/admin/recintos/{id}/baja")
    suspend fun darDeBaja(@Path("id") id: Long)

    // HU-34: cuentas de encargados
    @GET("api/admin/encargados")
    suspend fun encargados(): List<EncargadoDto>

    @POST("api/admin/encargados")
    suspend fun crearEncargado(@Body solicitud: EncargadoNuevoRequest): EncargadoDto

    @PATCH("api/admin/encargados/{id}/activo")
    suspend fun cambiarActivo(@Path("id") id: Long, @Body solicitud: CuentaActivaRequest): EncargadoDto
}
