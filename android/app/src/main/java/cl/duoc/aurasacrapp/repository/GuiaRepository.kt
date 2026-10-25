package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.model.Guia
import cl.duoc.aurasacrapp.model.SolicitudGuia

// Guías y solicitudes de guía. En esta entrega son datos fijos; en la prueba 3 vendrán del backend.
object GuiaRepository {

    private val guias = listOf(
        Guia(1, 1, "Marta", "Díaz", listOf("Sábado 10:00 a 13:00", "Domingo 09:00 a 12:00")),
        Guia(2, 1, "José", "Fuentes", listOf("Martes 16:00 a 19:00", "Jueves 16:00 a 19:00")),
        Guia(3, 3, "Rosa", "Pérez", listOf("Miércoles 10:00 a 14:00", "Viernes 10:00 a 14:00")),
        Guia(4, 3, "Tomás", "Vera", listOf("Sábado 11:00 a 17:00"))
    )

    private val solicitudes = listOf(
        SolicitudGuia(1, 1, "Colegio San Esteban", "3° básico B", "Martes 3 de noviembre · 10:00", 32),
        SolicitudGuia(2, 1, "Liceo Poeta Neruda", "1° medio A", "Jueves 12 de noviembre · 11:30", 28),
        SolicitudGuia(3, 3, "Colegio San Esteban", "6° básico A", "Viernes 6 de noviembre · 10:30", 30)
    )

    fun guiasDe(recintoId: Long): List<Guia> = guias.filter { it.recintoId == recintoId }

    fun solicitudesDe(recintoId: Long): List<SolicitudGuia> = solicitudes.filter { it.recintoId == recintoId }
}
