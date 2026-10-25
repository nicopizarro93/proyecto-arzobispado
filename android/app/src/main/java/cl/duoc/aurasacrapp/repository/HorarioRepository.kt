package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.model.Horario

// Horarios regulares. En esta entrega son datos fijos (los de la migración V4 del backend);
// en la prueba 3 se pedirán a la API.
object HorarioRepository {

    // Lunes a viernes, mañana y tarde
    private fun semana(vararg bloques: Pair<String, String>): List<Horario> =
        (1..5).flatMap { dia -> bloques.map { (desde, hasta) -> Horario(dia, desde, hasta) } }

    private val horarios: Map<Long, List<Horario>> = mapOf(
        // Capilla del Buen Pastor
        1L to semana("09:00" to "13:00", "16:00" to "19:00") +
            listOf(Horario(6, "10:00", "13:00"), Horario(7, "09:00", "12:00")),
        // Templo San Lucas del Llano
        2L to (1..6).map { Horario(it, "08:30", "12:30") } +
            listOf(Horario(7, "09:00", "13:00"), Horario(7, "18:00", "20:00")),
        // Museo de Arte Sacro Los Aromos: cerrado los lunes
        3L to (2..7).map { Horario(it, "10:00", "18:00") }
    )

    fun horariosDe(recintoId: Long): List<Horario> = horarios[recintoId] ?: emptyList()
}
