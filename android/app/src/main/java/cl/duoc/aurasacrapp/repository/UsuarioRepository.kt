package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.UsuarioDao
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.Usuario
import cl.duoc.aurasacrapp.remote.AuthApi
import cl.duoc.aurasacrapp.remote.EncargadoApi
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.dto.LoginRequest
import cl.duoc.aurasacrapp.remote.dto.RegistroRequest

// Repositorio: la única puerta de entrada a los datos de usuarios.
// Consulta al backend (Retrofit) y guarda una copia del usuario en Room.
// Si la llamada falla, lanza una excepción que el ViewModel convierte en un mensaje.
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val authApi: AuthApi = RetrofitClient.authApi,
    private val encargadoApi: EncargadoApi = RetrofitClient.encargadoApi
) {

    // Inicia sesión en el backend, guarda el usuario y deja la sesión abierta
    suspend fun iniciarSesion(correo: String, password: String): Usuario {
        val respuesta = authApi.login(LoginRequest(correo.trim(), password))
        // El token se necesita antes de pedir datos protegidos (el recinto del encargado)
        SesionRepository.guardarToken(respuesta.token)
        try {
            val dto = respuesta.usuario
            val recintoId = if (dto.rol == Rol.ENCARGADO) encargadoApi.miRecinto().id else null
            val usuario = Usuario(
                id = dto.id,
                rol = dto.rol,
                recintoId = recintoId,
                nombre = dto.nombre,
                apellido = dto.apellido,
                correo = dto.correo,
                institucion = dto.institucion
            )
            usuarioDao.usuarioGuardar(usuario)
            SesionRepository.iniciar(usuario, respuesta.token)
            return usuario
        } catch (e: Exception) {
            SesionRepository.cerrar()
            throw e
        }
    }

    // Crea la cuenta en el backend (la contraseña la cifra el servidor con BCrypt)
    suspend fun registrar(solicitud: RegistroRequest) {
        authApi.registrar(solicitud)
    }
}
