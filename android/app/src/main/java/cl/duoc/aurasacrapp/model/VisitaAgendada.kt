package cl.duoc.aurasacrapp.model

enum class EstadoVisita {
    AGENDADA, REALIZADA, CANCELADA
}

// Visita planificada por el usuario (a un recinto o a una ruta). Vive en el backend:
// no es una tabla de Room. No se confunde con Visita, que es el registro local de un QR escaneado.
data class VisitaAgendada(
    val id: Long,
    val recintoId: Long?,          // null si la visita es a una ruta
    val destino: String,           // nombre del recinto o de la ruta
    val inicio: Long,              // en milisegundos
    val cantidadPersonas: Int,
    val nivelCurso: String?,       // solo las salidas pedagógicas de docentes
    val observaciones: String?,    // notas de quien agenda
    val estado: EstadoVisita
)
