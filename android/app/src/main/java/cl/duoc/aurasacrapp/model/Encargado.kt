package cl.duoc.aurasacrapp.model

// Cuenta de un encargado de recinto, vista por el administrador. Viene del backend (no es tabla de Room).
data class Encargado(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val activo: Boolean,
    val recinto: String?
)
