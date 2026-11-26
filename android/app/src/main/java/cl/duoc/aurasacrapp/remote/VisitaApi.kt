package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.VisitaDto
import cl.duoc.aurasacrapp.remote.dto.VisitaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Visitas agendadas (solo visitantes y docentes con sesión iniciada)
interface VisitaApi {

    @POST("api/visitas")
    suspend fun agendar(@Body solicitud: VisitaRequest): VisitaDto

    @GET("api/visitas")
    suspend fun misVisitas(): List<VisitaDto>

    @PATCH("api/visitas/{id}/cancelar")
    suspend fun cancelar(@Path("id") id: Long): VisitaDto
}
