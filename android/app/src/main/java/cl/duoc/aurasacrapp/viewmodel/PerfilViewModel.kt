package cl.duoc.aurasacrapp.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.aurasacrapp.model.Usuario
import cl.duoc.aurasacrapp.repository.SesionRepository
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel : ViewModel() {

    // El usuario que inició sesión (o null si no hay sesión)
    val usuario: StateFlow<Usuario?> = SesionRepository.usuario

    fun cerrarSesion() {
        SesionRepository.cerrar()
    }
}
