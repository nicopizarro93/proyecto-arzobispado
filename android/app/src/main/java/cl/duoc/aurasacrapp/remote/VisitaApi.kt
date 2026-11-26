package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.CambioVisitaRequest
import cl.duoc.aurasacrapp.remote.dto.SolicitudGuiaRequest
import cl.duoc.aurasacrapp.remote.dto.SolicitudUsuarioDto
import cl.duoc.aurasacrapp.remote.dto.VisitaDto
import cl.duoc.aurasacrapp.remote.dto.VisitaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Visitas agendadas (solo visitantes y docentes con sesión iniciada)
interface VisitaApi {

    @POST("api/visitas")
    suspend fun agendar(@Body solicitud: VisitaRequest): VisitaDto

    @GET("api/visitas")
    suspend fun misVisitas(): List<VisitaDto>

    // Cambia la fecha, la hora y la cantidad de personas de una visita agendada
    @PUT("api/visitas/{id}")
    suspend fun cambiar(@Path("id") id: Long, @Body solicitud: CambioVisitaRequest): VisitaDto

    @PATCH("api/visitas/{id}/cancelar")
    suspend fun cancelar(@Path("id") id: Long): VisitaDto

    // Guías: pedir un guía para un recinto de la visita, y ver mis solicitudes
    @POST("api/visitas/{id}/solicitudes-guia")
    suspend fun solicitarGuia(@Path("id") id: Long, @Body solicitud: SolicitudGuiaRequest): SolicitudUsuarioDto

    @GET("api/visitas/solicitudes-guia")
    suspend fun misSolicitudesGuia(): List<SolicitudUsuarioDto>
}
