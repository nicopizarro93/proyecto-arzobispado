package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.Rol

// Datos que viajan en JSON entre la app y el backend (login y registro).
// Los nombres de los campos son los mismos que usa la API.

data class LoginRequest(
    val correo: String,
    val password: String
)

data class RegistroRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val confirmarPassword: String,
    val rol: Rol,
    val institucion: String?,
    val aceptaPrivacidad: Boolean
)

// Usuario tal como lo entrega el backend (no incluye contraseña)
data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rol: Rol,
    val institucion: String?
)

data class LoginResponse(
    val token: String,
    val tipo: String,
    val expiraEn: String,
    val usuario: UsuarioDto
)

// Del recinto de un encargado solo necesitamos su id
data class RecintoIdDto(
    val id: Long
)
