package cl.duoc.aurasacrapp.model

// Evento visto por el encargado en su panel (incluye los cancelados).
// No es una tabla de Room: viene del backend.
data class EventoGestion(
    val id: Long,
    val tipo: TipoEvento,
    val titulo: String,
    val descripcion: String?,
    val inicio: Long,          // en milisegundos
    val fin: Long?,            // null = sin hora de término
    val estado: EstadoEvento
)

// Ficha del recinto para editarla: la reseña y los bloques de horario
data class FichaGestion(
    val recintoId: Long,
    val historia: String,
    val horarios: List<Horario>
)
