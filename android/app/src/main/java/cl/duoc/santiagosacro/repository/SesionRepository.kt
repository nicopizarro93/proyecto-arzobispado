package cl.duoc.santiagosacro.repository

import cl.duoc.santiagosacro.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Guarda en memoria quién inició sesión.
// Es un object (singleton): hay una sola sesión para toda la app.
object SesionRepository {

    // _usuario se puede modificar solo aquí adentro; afuera se expone de solo lectura
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    fun iniciar(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrar() {
        _usuario.value = null
    }
}
