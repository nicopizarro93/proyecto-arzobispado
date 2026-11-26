package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.remote.dto.RegistroRequest
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla Crear cuenta
data class RegistroUiState(
    val esDocente: Boolean = false,
    val nombre: String = "",
    val apellido: String = "",
    val institucion: String = "",
    val correo: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val aceptaPrivacidad: Boolean = false,
    val errores: Map<String, String> = emptyMap(),   // un mensaje por campo
    val cargando: Boolean = false,
    val error: String? = null,                       // error general (por ejemplo, correo repetido)
    val cuentaCreada: Boolean = false
)

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onTipoCuentaChange(esDocente: Boolean) = _uiState.update { it.copy(esDocente = esDocente) }
    fun onNombreChange(valor: String) = _uiState.update { it.copy(nombre = valor, errores = it.errores - "nombre") }
    fun onApellidoChange(valor: String) = _uiState.update { it.copy(apellido = valor, errores = it.errores - "apellido") }
    fun onInstitucionChange(valor: String) = _uiState.update { it.copy(institucion = valor, errores = it.errores - "institucion") }
    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor, errores = it.errores - "correo", error = null) }
    fun onPasswordChange(valor: String) = _uiState.update { it.copy(password = valor, errores = it.errores - "password") }
    fun onConfirmarChange(valor: String) = _uiState.update { it.copy(confirmarPassword = valor, errores = it.errores - "confirmar") }
    fun onPrivacidadChange(valor: Boolean) = _uiState.update { it.copy(aceptaPrivacidad = valor) }

    // Mismas reglas que valida el backend, para avisar antes de enviar
    private fun validar(estado: RegistroUiState): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        if (estado.nombre.trim().length !in 2..50) {
            errores["nombre"] = "El nombre debe tener entre 2 y 50 caracteres"
        }
        if (estado.apellido.trim().length !in 2..50) {
            errores["apellido"] = "El apellido debe tener entre 2 y 50 caracteres"
        }
        if (estado.esDocente && estado.institucion.trim().length !in 2..120) {
            errores["institucion"] = "Indica tu colegio o institución (mínimo 2 caracteres)"
        }
        if (!CORREO.matches(estado.correo.trim().lowercase())) {
            errores["correo"] = "El correo no tiene un formato válido"
        }
        if (estado.password.length !in 8..72 ||
            estado.password.none { it.isLetter() } || estado.password.none { it.isDigit() }
        ) {
            errores["password"] = "Mínimo 8 caracteres, con al menos una letra y un número"
        }
        if (estado.password != estado.confirmarPassword) {
            errores["confirmar"] = "Las contraseñas no coinciden"
        }
        return errores
    }

    fun crearCuenta() {
        val estado = _uiState.value
        val errores = validar(estado)
        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores, error = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                repositorio.registrar(
                    RegistroRequest(
                        nombre = estado.nombre.trim(),
                        apellido = estado.apellido.trim(),
                        correo = estado.correo.trim().lowercase(),
                        password = estado.password,
                        confirmarPassword = estado.confirmarPassword,
                        rol = if (estado.esDocente) Rol.DOCENTE else Rol.VISITANTE,
                        institucion = if (estado.esDocente) estado.institucion.trim() else null,
                        aceptaPrivacidad = estado.aceptaPrivacidad
                    )
                )
                _uiState.update { it.copy(cargando = false, cuentaCreada = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    private companion object {
        val CORREO = Regex("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    }
}
