package cl.duoc.aurasacrapp.remote.dto

// Datos del perfil tal como viajan entre la app y el backend

// Lo que el usuario puede cambiar de su perfil (el correo no se cambia)
data class PerfilRequest(
    val nombre: String,
    val apellido: String,
    val institucion: String?
)

data class CambioPasswordRequest(
    val passwordActual: String,
    val passwordNueva: String,
    val confirmarPassword: String
)
