package cl.duoc.aurasacrapp.model

// Guía voluntario de un recinto. No es usuario de la app: lo administra el encargado.
data class Guia(
    val id: Long,
    val recintoId: Long,
    val nombre: String,
    val apellido: String,
    val disponibilidad: List<String>      // bloques, por ejemplo "Sábado 10:00 a 13:00"
)

// Solicitud de guía que hace un colegio al planificar su visita
data class SolicitudGuia(
    val id: Long,
    val recintoId: Long,
    val institucion: String,
    val curso: String,
    val fecha: String,
    val personas: Int
)
