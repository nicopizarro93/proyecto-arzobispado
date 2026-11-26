package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.Usuario
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla Mi perfil: los datos de la cuenta y los dos formularios
data class PerfilUiState(
    val usuario: Usuario? = null,
    // Formulario de datos personales
    val nombre: String = "",
    val apellido: String = "",
    val institucion: String = "",
    val erroresDatos: Map<String, String> = emptyMap(),
    val guardandoDatos: Boolean = false,
    val errorDatos: String? = null,              // error del servidor
    // Formulario de cambio de contraseña
    val passwordActual: String = "",
    val passwordNueva: String = "",
    val confirmarPassword: String = "",
    val erroresPassword: Map<String, String> = emptyMap(),
    val guardandoPassword: Boolean = false,
    val errorPassword: String? = null,           // error del servidor
    val mensaje: String? = null                  // aviso corto de lo que se guardó
) {
    val esDocente: Boolean get() = usuario?.rol == Rol.DOCENTE
}

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    // Los formularios parten con los datos actuales de la cuenta
    private val _uiState = MutableStateFlow(
        SesionRepository.usuario.value.let { u ->
            PerfilUiState(
                usuario = u,
                nombre = u?.nombre.orEmpty(),
                apellido = u?.apellido.orEmpty(),
                institucion = u?.institucion.orEmpty()
            )
        }
    )
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun onNombreChange(valor: String) = _uiState.update { it.copy(nombre = valor, erroresDatos = it.erroresDatos - "nombre") }
    fun onApellidoChange(valor: String) = _uiState.update { it.copy(apellido = valor, erroresDatos = it.erroresDatos - "apellido") }
    fun onInstitucionChange(valor: String) = _uiState.update { it.copy(institucion = valor, erroresDatos = it.erroresDatos - "institucion") }
    fun onPasswordActualChange(valor: String) = _uiState.update { it.copy(passwordActual = valor, erroresPassword = it.erroresPassword - "actual", errorPassword = null) }
    fun onPasswordNuevaChange(valor: String) = _uiState.update { it.copy(passwordNueva = valor, erroresPassword = it.erroresPassword - "nueva") }
    fun onConfirmarChange(valor: String) = _uiState.update { it.copy(confirmarPassword = valor, erroresPassword = it.erroresPassword - "confirmar") }
    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun guardarDatos() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()
        if (estado.nombre.trim().length !in 2..50) errores["nombre"] = "El nombre debe tener entre 2 y 50 caracteres"
        if (estado.apellido.trim().length !in 2..50) errores["apellido"] = "El apellido debe tener entre 2 y 50 caracteres"
        if (estado.esDocente && estado.institucion.trim().length !in 2..120) {
            errores["institucion"] = "Indica tu colegio o institución (mínimo 2 caracteres)"
        }
        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(erroresDatos = errores, errorDatos = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(guardandoDatos = true, errorDatos = null) }
            try {
                val nuevo = repositorio.actualizarPerfil(
                    estado.nombre, estado.apellido, if (estado.esDocente) estado.institucion else null
                )
                _uiState.update { it.copy(guardandoDatos = false, usuario = nuevo, mensaje = "Datos actualizados") }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardandoDatos = false, errorDatos = mensajeDeError(e)) }
            }
        }
    }

    fun cambiarPassword() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()
        if (estado.passwordActual.isEmpty()) errores["actual"] = "Escribe tu contraseña actual"
        val nueva = estado.passwordNueva
        if (nueva.length !in 8..72 || nueva.none { it.isLetter() } || nueva.none { it.isDigit() }) {
            errores["nueva"] = "Mínimo 8 caracteres, con al menos una letra y un número"
        } else if (nueva == estado.passwordActual) {
            errores["nueva"] = "La contraseña nueva debe ser distinta de la actual"
        }
        if (nueva != estado.confirmarPassword) errores["confirmar"] = "Las contraseñas no coinciden"
        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(erroresPassword = errores, errorPassword = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(guardandoPassword = true, errorPassword = null) }
            try {
                repositorio.cambiarPassword(estado.passwordActual, nueva, estado.confirmarPassword)
                // Se vacían los campos: la contraseña no debe quedar en pantalla
                _uiState.update {
                    it.copy(
                        guardandoPassword = false, passwordActual = "", passwordNueva = "",
                        confirmarPassword = "", mensaje = "Contraseña actualizada"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardandoPassword = false, errorPassword = mensajeDeError(e)) }
            }
        }
    }

    fun cerrarSesion() {
        SesionRepository.cerrar()
    }
}
