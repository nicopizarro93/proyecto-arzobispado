package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.model.BloqueDisponibilidad
import cl.duoc.aurasacrapp.model.Guia
import cl.duoc.aurasacrapp.model.SolicitudRecinto
import cl.duoc.aurasacrapp.model.SolicitudUsuario
import cl.duoc.aurasacrapp.remote.EncargadoApi
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.VisitaApi
import cl.duoc.aurasacrapp.remote.dto.AsignarGuiaRequest
import cl.duoc.aurasacrapp.remote.dto.DisponibilidadRequest
import cl.duoc.aurasacrapp.remote.dto.GuiaActivoRequest
import cl.duoc.aurasacrapp.remote.dto.GuiaDto
import cl.duoc.aurasacrapp.remote.dto.GuiaRequest
import cl.duoc.aurasacrapp.remote.dto.RechazarSolicitudRequest
import cl.duoc.aurasacrapp.remote.dto.SolicitudGuiaRequest
import cl.duoc.aurasacrapp.remote.dto.SolicitudRecintoDto
import cl.duoc.aurasacrapp.remote.dto.SolicitudUsuarioDto
import cl.duoc.aurasacrapp.util.isoAMillis
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Guías y solicitudes de guía. Todo vive en el backend:
// - el encargado administra los guías de su recinto y responde las solicitudes;
// - el visitante o el docente pide un guía para su visita y ve cómo va la solicitud.
class GuiaRepository(
    private val encargadoApi: EncargadoApi = RetrofitClient.encargadoApi,
    private val visitaApi: VisitaApi = RetrofitClient.visitaApi
) {

    // ---- Encargado: guías y disponibilidad ----

    suspend fun guias(): List<Guia> = encargadoApi.guias().map { it.aModelo() }

    suspend fun crearGuia(nombre: String, apellido: String): Guia =
        encargadoApi.crearGuia(GuiaRequest(nombre.trim(), apellido.trim())).aModelo()

    suspend fun cambiarActivo(guiaId: Long, activo: Boolean): Guia =
        encargadoApi.cambiarGuiaActivo(guiaId, GuiaActivoRequest(activo)).aModelo()

    suspend fun agregarDisponibilidad(guiaId: Long, inicio: LocalDateTime, fin: LocalDateTime): Guia =
        encargadoApi.agregarDisponibilidad(
            guiaId, DisponibilidadRequest(FECHA_HORA.format(inicio), FECHA_HORA.format(fin))
        ).aModelo()

    suspend fun eliminarDisponibilidad(guiaId: Long, bloqueId: Long): Guia =
        encargadoApi.eliminarDisponibilidad(guiaId, bloqueId).aModelo()

    // ---- Encargado: solicitudes ----

    suspend fun solicitudesDelRecinto(): List<SolicitudRecinto> =
        encargadoApi.solicitudesGuia().map { it.aModelo() }

    // Los guías activos que están disponibles en el horario de esa solicitud
    suspend fun guiasDisponibles(solicitudId: Long): List<Guia> =
        encargadoApi.guiasDisponibles(solicitudId).map { it.aModelo() }

    suspend fun asignar(solicitudId: Long, guiaId: Long) {
        encargadoApi.asignarGuia(solicitudId, AsignarGuiaRequest(guiaId))
    }

    suspend fun rechazar(solicitudId: Long, motivo: String) {
        encargadoApi.rechazarSolicitud(solicitudId, RechazarSolicitudRequest(motivo.trim()))
    }

    // ---- Visitante o docente ----

    suspend fun solicitarGuia(visitaId: Long, recintoId: Long) {
        visitaApi.solicitarGuia(visitaId, SolicitudGuiaRequest(recintoId))
    }

    suspend fun misSolicitudes(): List<SolicitudUsuario> = visitaApi.misSolicitudesGuia().map { it.aModelo() }

    // ---- Conversión de los datos del backend ----

    private fun GuiaDto.aModelo() = Guia(
        id = id,
        nombre = nombre,
        apellido = apellido,
        activo = activo,
        disponibilidad = disponibilidad.map { BloqueDisponibilidad(it.id, isoAMillis(it.inicio), isoAMillis(it.fin)) }
    )

    private fun SolicitudRecintoDto.aModelo() = SolicitudRecinto(
        id = id,
        estado = estado,
        inicio = isoAMillis(inicio),
        personas = cantidadPersonas,
        curso = nivelCurso,
        solicitante = solicitante,
        institucion = institucion,
        guia = guia,
        motivoRechazo = motivoRechazo
    )

    private fun SolicitudUsuarioDto.aModelo() = SolicitudUsuario(
        id = id,
        visitaId = idVisita,
        recinto = recinto,
        estado = estado,
        guia = guia,
        motivoRechazo = motivoRechazo
    )

    private companion object {
        val FECHA_HORA: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}
