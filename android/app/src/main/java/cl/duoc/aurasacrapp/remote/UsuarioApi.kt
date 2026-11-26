package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.CambioPasswordRequest
import cl.duoc.aurasacrapp.remote.dto.PerfilRequest
import cl.duoc.aurasacrapp.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.PUT

// Perfil del usuario que inició sesión (exige el token JWT)
interface UsuarioApi {

    @PUT("api/usuarios/yo")
    suspend fun actualizarPerfil(@Body solicitud: PerfilRequest): UsuarioDto

    // Responde 204 sin contenido si la contraseña se cambió
    @PUT("api/usuarios/yo/password")
    suspend fun cambiarPassword(@Body solicitud: CambioPasswordRequest)
}
