package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.AsignarGuiaRequest
import cl.duoc.aurasacrapp.remote.dto.DisponibilidadRequest
import cl.duoc.aurasacrapp.remote.dto.GuiaActivoRequest
import cl.duoc.aurasacrapp.remote.dto.GuiaDto
import cl.duoc.aurasacrapp.remote.dto.GuiaRequest
import cl.duoc.aurasacrapp.remote.dto.RechazarSolicitudRequest
import cl.duoc.aurasacrapp.remote.dto.SolicitudRecintoDto
import cl.duoc.aurasacrapp.remote.dto.CambioEstadoRequest
import cl.duoc.aurasacrapp.remote.dto.EventoGestionDto
import cl.duoc.aurasacrapp.remote.dto.EventoRequest
import cl.duoc.aurasacrapp.remote.dto.FichaRequest
import cl.duoc.aurasacrapp.remote.dto.HorarioDto
import cl.duoc.aurasacrapp.remote.dto.HorarioRequest
import cl.duoc.aurasacrapp.remote.dto.RecintoDetalleDto
import cl.duoc.aurasacrapp.remote.dto.RecintoIdDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del panel del encargado (exigen el token JWT de un encargado).
// El recinto sale de la cuenta del token: el encargado solo puede modificar el suyo.
interface EncargadoApi {

    // Solo el id del recinto asignado (se usa al iniciar sesión)
    @GET("api/encargado/recinto")
    suspend fun miRecinto(): RecintoIdDto

    // Ficha completa del recinto asignado: reseña, estado y horarios
    @GET("api/encargado/recinto")
    suspend fun miRecintoDetalle(): RecintoDetalleDto

    @PATCH("api/encargado/recinto/estado")
    suspend fun cambiarEstado(@Body solicitud: CambioEstadoRequest): RecintoDetalleDto

    @PUT("api/encargado/recinto/ficha")
    suspend fun editarFicha(@Body solicitud: FichaRequest): RecintoDetalleDto

    // ---- Horarios ----

    @POST("api/encargado/recinto/horarios")
    suspend fun agregarHorario(@Body solicitud: HorarioRequest): HorarioDto

    @PUT("api/encargado/recinto/horarios/{id}")
    suspend fun editarHorario(@Path("id") id: Long, @Body solicitud: HorarioRequest): HorarioDto

    @DELETE("api/encargado/recinto/horarios/{id}")
    suspend fun eliminarHorario(@Path("id") id: Long)

    // ---- Eventos, avisos, aperturas y cierres ----

    @GET("api/encargado/recinto/eventos")
    suspend fun eventos(): List<EventoGestionDto>

    @POST("api/encargado/recinto/eventos")
    suspend fun publicarEvento(@Body solicitud: EventoRequest): EventoGestionDto

    @PUT("api/encargado/recinto/eventos/{id}")
    suspend fun editarEvento(@Path("id") id: Long, @Body solicitud: EventoRequest): EventoGestionDto

    @PATCH("api/encargado/recinto/eventos/{id}/cancelar")
    suspend fun cancelarEvento(@Path("id") id: Long): EventoGestionDto

    // ---- Guías (HU-32) y solicitudes de guía (HU-33) ----

    @GET("api/encargado/recinto/guias")
    suspend fun guias(): List<GuiaDto>

    @POST("api/encargado/recinto/guias")
    suspend fun crearGuia(@Body solicitud: GuiaRequest): GuiaDto

    @PATCH("api/encargado/recinto/guias/{id}/activo")
    suspend fun cambiarGuiaActivo(@Path("id") id: Long, @Body solicitud: GuiaActivoRequest): GuiaDto

    @POST("api/encargado/recinto/guias/{id}/disponibilidad")
    suspend fun agregarDisponibilidad(@Path("id") id: Long, @Body solicitud: DisponibilidadRequest): GuiaDto

    @DELETE("api/encargado/recinto/guias/{id}/disponibilidad/{idBloque}")
    suspend fun eliminarDisponibilidad(@Path("id") id: Long, @Path("idBloque") idBloque: Long): GuiaDto

    @GET("api/encargado/recinto/solicitudes-guia")
    suspend fun solicitudesGuia(): List<SolicitudRecintoDto>

    // Guías activos que están disponibles en el horario de la solicitud
    @GET("api/encargado/recinto/solicitudes-guia/{id}/guias-disponibles")
    suspend fun guiasDisponibles(@Path("id") id: Long): List<GuiaDto>

    @PATCH("api/encargado/recinto/solicitudes-guia/{id}/asignar")
    suspend fun asignarGuia(@Path("id") id: Long, @Body solicitud: AsignarGuiaRequest): SolicitudRecintoDto

    @PATCH("api/encargado/recinto/solicitudes-guia/{id}/rechazar")
    suspend fun rechazarSolicitud(@Path("id") id: Long, @Body solicitud: RechazarSolicitudRequest): SolicitudRecintoDto
}
