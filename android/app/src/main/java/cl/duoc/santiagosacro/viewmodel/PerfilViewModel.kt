package cl.duoc.santiagosacro.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.santiagosacro.model.Usuario
import cl.duoc.santiagosacro.repository.SesionRepository
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel : ViewModel() {

    // El usuario que inició sesión (o null si no hay sesión)
    val usuario: StateFlow<Usuario?> = SesionRepository.usuario

    fun cerrarSesion() {
        SesionRepository.cerrar()
    }
}
