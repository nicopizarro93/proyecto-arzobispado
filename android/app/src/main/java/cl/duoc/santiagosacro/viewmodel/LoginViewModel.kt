package cl.duoc.santiagosacro.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.santiagosacro.database.AppDatabase
import cl.duoc.santiagosacro.repository.SesionRepository
import cl.duoc.santiagosacro.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla de login: todo lo que la vista necesita para dibujarse
data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val ingresoExitoso: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // El ViewModel le pide los datos al repositorio, nunca directo al DAO
    private val repositorio = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, error = null) }
    }

    fun onPasswordChange(valor: String) {
        _uiState.update { it.copy(password = valor, error = null) }
    }

    fun ingresar() {
        val estado = _uiState.value

        // 1. Validaciones del formulario (antes de consultar la base de datos)
        if (estado.correo.isBlank() || estado.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu correo y tu contraseña") }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(estado.correo.trim()).matches()) {
            _uiState.update { it.copy(error = "El correo no tiene un formato válido") }
            return
        }

        // 2. Consulta a Room en una corrutina (no bloquea la pantalla)
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val usuario = repositorio.validarCredenciales(estado.correo, estado.password)
            if (usuario != null) {
                SesionRepository.iniciar(usuario)
                _uiState.update { it.copy(cargando = false, ingresoExitoso = true) }
            } else {
                _uiState.update { it.copy(cargando = false, error = "Correo o contraseña incorrectos") }
            }
        }
    }
}
