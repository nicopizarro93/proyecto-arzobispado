package cl.duoc.aurasacrapp.model

// Un bloque de atención de un recinto. diaSemana: 1 = lunes ... 7 = domingo (como en el backend).
// Un día puede tener dos bloques (mañana y tarde).
data class Horario(
    val diaSemana: Int,
    val apertura: String,
    val cierre: String
)
