package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Encargado
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.AdminRepository
import cl.duoc.aurasacrapp.repository.RecintoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la sección Encargados del panel del administrador (HU-34)
data class AdminEncargadosUiState(
    val cargando: Boolean = true,
    val encargados: List<Encargado> = emptyList(),
    val recintos: List<RecintoConComuna> = emptyList(),     // para elegir el recinto de la cuenta nueva
    // Formulario de la cuenta nueva
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val password: String = "",
    val recinto: RecintoConComuna? = null,
    val errores: Map<String, String> = emptyMap(),
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

class AdminEncargadosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val recintoRepositorio = RecintoRepository(db.recintoDao(), db.comunaDao())
    private val repositorio = AdminRepository(recintoRepositorio)

    // Todo lo que viene del backend más lo que el administrador va escribiendo
    private val estado = MutableStateFlow(AdminEncargadosUiState())

    val uiState: StateFlow<AdminEncargadosUiState> = combine(recintoRepositorio.recintos, estado) { recintos, e ->
        e.copy(recintos = recintos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminEncargadosUiState()
    )

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            try {
                val lista = repositorio.encargados()
                estado.update { it.copy(cargando = false, encargados = lista, error = null) }
            } catch (e: Exception) {
                estado.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun onNombreChange(v: String) = estado.update { it.copy(nombre = v, errores = it.errores - "nombre") }
    fun onApellidoChange(v: String) = estado.update { it.copy(apellido = v, errores = it.errores - "apellido") }
    fun onCorreoChange(v: String) = estado.update { it.copy(correo = v, errores = it.errores - "correo", error = null) }
    fun onPasswordChange(v: String) = estado.update { it.copy(password = v, errores = it.errores - "password") }
    fun onRecintoChange(r: RecintoConComuna) = estado.update { it.copy(recinto = r, errores = it.errores - "recinto") }
    fun mensajeMostrado() = estado.update { it.copy(mensaje = null) }

    fun crear() {
        val f = estado.value
        val errores = mutableMapOf<String, String>()
        if (f.nombre.trim().length !in 2..50) errores["nombre"] = "El nombre debe tener entre 2 y 50 caracteres"
        if (f.apellido.trim().length !in 2..50) errores["apellido"] = "El apellido debe tener entre 2 y 50 caracteres"
        if (!CORREO.matches(f.correo.trim().lowercase())) errores["correo"] = "El correo no tiene un formato válido"
        if (f.password.length !in 8..72 || f.password.none { it.isLetter() } || f.password.none { it.isDigit() }) {
            errores["password"] = "Mínimo 8 caracteres, con al menos una letra y un número"
        }
        if (f.recinto == null) errores["recinto"] = "Elige el recinto que tendrá a cargo"
        if (errores.isNotEmpty() || f.recinto == null) {
            estado.update { it.copy(errores = errores, error = null) }
            return
        }
        ejecutar("Cuenta de encargado creada") {
            repositorio.crearEncargado(f.nombre, f.apellido, f.correo, f.password, f.recinto.id)
            estado.update { it.copy(nombre = "", apellido = "", correo = "", password = "", recinto = null) }
        }
    }

    fun cambiarActivo(encargado: Encargado) {
        ejecutar(if (encargado.activo) "Cuenta desactivada" else "Cuenta activada") {
            repositorio.cambiarActivo(encargado.id, !encargado.activo)
        }
    }

    // Ejecuta un cambio en el backend, vuelve a pedir la lista y avisa el resultado
    private fun ejecutar(aviso: String, accion: suspend () -> Unit) {
        viewModelScope.launch {
            estado.update { it.copy(guardando = true, error = null) }
            try {
                accion()
                val lista = repositorio.encargados()
                estado.update { it.copy(guardando = false, encargados = lista, mensaje = aviso) }
            } catch (e: Exception) {
                estado.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    private companion object {
        val CORREO = Regex("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    }
}
