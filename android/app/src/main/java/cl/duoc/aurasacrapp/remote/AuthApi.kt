package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.remote.dto.LoginRequest
import cl.duoc.aurasacrapp.remote.dto.LoginResponse
import cl.duoc.aurasacrapp.remote.dto.RegistroRequest
import cl.duoc.aurasacrapp.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.POST

// Endpoints de autenticación del backend (públicos, no necesitan token)
interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body solicitud: LoginRequest): LoginResponse

    @POST("api/auth/registro")
    suspend fun registrar(@Body solicitud: RegistroRequest): UsuarioDto
}
