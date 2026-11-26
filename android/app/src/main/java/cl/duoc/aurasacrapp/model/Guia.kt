package cl.duoc.aurasacrapp.model

// Guías voluntarios y solicitudes de guía. No son tablas de Room: viven en el backend.

enum class EstadoSolicitud {
    PENDIENTE, ASIGNADA, RECHAZADA, CANCELADA
}

// Bloque de tiempo en que un guía está disponible (fechas en milisegundos)
data class BloqueDisponibilidad(
    val id: Long,
    val inicio: Long,
    val fin: Long
)

// Guía voluntario de un recinto. No es usuario de la app: lo administra el encargado.
data class Guia(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val activo: Boolean,
    val disponibilidad: List<BloqueDisponibilidad>
)

// Solicitud de guía vista por el encargado: la visita (fecha, hora y personas) y su estado
data class SolicitudRecinto(
    val id: Long,
    val estado: EstadoSolicitud,
    val inicio: Long,
    val personas: Int,
    val curso: String?,              // solo en salidas pedagógicas de docentes
    val solicitante: String,
    val institucion: String?,
    val guia: String?,               // guía asignado
    val motivoRechazo: String?
)

// Solicitud de guía vista por quien la pidió: estado, guía asignado o motivo del rechazo
data class SolicitudUsuario(
    val id: Long,
    val visitaId: Long,
    val recinto: String,
    val estado: EstadoSolicitud,
    val guia: String?,
    val motivoRechazo: String?
)
