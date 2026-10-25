package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.dao.UsuarioDao
import cl.duoc.aurasacrapp.model.Usuario
import cl.duoc.aurasacrapp.util.hashPassword

// Repositorio: la única puerta de entrada a los datos de usuarios.
// Hoy consulta Room; en la prueba 3 podrá consultar al backend sin cambiar los ViewModel ni las pantallas.
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Devuelve el usuario si el correo y la contraseña coinciden; si no, devuelve null
    suspend fun validarCredenciales(correo: String, password: String): Usuario? {
        val usuario = usuarioDao.usuarioBuscarPorCorreo(correo.trim().lowercase()) ?: return null
        if (!usuario.activo) return null
        // Se compara el hash de lo que escribió con el hash guardado (nunca la contraseña en texto plano)
        return if (usuario.passwordHash == hashPassword(password)) usuario else null
    }
}
