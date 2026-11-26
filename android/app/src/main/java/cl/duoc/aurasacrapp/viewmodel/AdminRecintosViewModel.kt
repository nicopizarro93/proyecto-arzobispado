package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Comuna
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.TipoRecinto
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

// Estado de la sección Recintos del panel del administrador: el formulario para registrar uno
// y la lista de los recintos activos (para darlos de baja)
data class AdminRecintosUiState(
    val tipo: TipoRecinto = TipoRecinto.TEMPLO,
    val nombre: String = "",
    val comuna: Comuna? = null,
    val calle: String = "",
    val numero: String = "",
    val latitud: String = "",
    val longitud: String = "",
    val historia: String = "",
    val errores: Map<String, String> = emptyMap(),   // un mensaje por campo
    val comunas: List<Comuna> = emptyList(),
    val recintos: List<RecintoConComuna> = emptyList(),
    val confirmandoBajaId: Long? = null,             // recinto que se está por dar de baja
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null                        // error del servidor
)

class AdminRecintosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val recintoRepositorio = RecintoRepository(db.recintoDao(), db.comunaDao())
    private val repositorio = AdminRepository(recintoRepositorio)

    // Lo que el administrador va escribiendo
    private val formulario = MutableStateFlow(AdminRecintosUiState())

    val uiState: StateFlow<AdminRecintosUiState> = combine(
        recintoRepositorio.recintos, db.comunaDao().comunaListar(), formulario
    ) { recintos, comunas, f ->
        f.copy(recintos = recintos, comunas = comunas)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminRecintosUiState()
    )

    fun onTipoChange(tipo: TipoRecinto) = formulario.update { it.copy(tipo = tipo) }
    fun onNombreChange(v: String) = formulario.update { it.copy(nombre = v, errores = it.errores - "nombre") }
    fun onComunaChange(comuna: Comuna) = formulario.update { it.copy(comuna = comuna, errores = it.errores - "comuna") }
    fun onCalleChange(v: String) = formulario.update { it.copy(calle = v, errores = it.errores - "calle") }
    fun onNumeroChange(v: String) = formulario.update { it.copy(numero = v, errores = it.errores - "numero") }
    fun onLatitudChange(v: String) = formulario.update { it.copy(latitud = v, errores = it.errores - "latitud") }
    fun onLongitudChange(v: String) = formulario.update { it.copy(longitud = v, errores = it.errores - "longitud") }
    fun onHistoriaChange(v: String) = formulario.update { it.copy(historia = v, errores = it.errores - "historia") }
    fun mensajeMostrado() = formulario.update { it.copy(mensaje = null) }

    fun registrar() {
        val f = formulario.value
        val errores = mutableMapOf<String, String>()
        if (f.nombre.isBlank() || f.nombre.trim().length > 150) errores["nombre"] = "El nombre oficial es obligatorio (máximo 150 caracteres)"
        if (f.comuna == null) errores["comuna"] = "Elige la comuna"
        if (f.calle.isBlank() || f.calle.trim().length > 150) errores["calle"] = "La calle es obligatoria (máximo 150 caracteres)"
        if (f.numero.trim().length > 10) errores["numero"] = "El número admite hasta 10 caracteres"
        val latitud = f.latitud.trim().replace(',', '.').toDoubleOrNull()
        val longitud = f.longitud.trim().replace(',', '.').toDoubleOrNull()
        if (latitud == null || latitud !in -90.0..90.0) errores["latitud"] = "La latitud debe ser un número entre -90 y 90"
        if (longitud == null || longitud !in -180.0..180.0) errores["longitud"] = "La longitud debe ser un número entre -180 y 180"
        if (f.historia.trim().length > 5000) errores["historia"] = "Máximo 5000 caracteres"
        if (errores.isNotEmpty() || f.comuna == null || latitud == null || longitud == null) {
            formulario.update { it.copy(errores = errores, error = null) }
            return
        }
        ejecutar("Recinto registrado") {
            repositorio.registrarRecinto(f.tipo, f.nombre, f.comuna.id, f.calle, f.numero, latitud, longitud, f.historia)
            // Se vacía el formulario: el recinto ya quedó en la red
            formulario.update {
                it.copy(nombre = "", calle = "", numero = "", latitud = "", longitud = "", historia = "", comuna = null)
            }
        }
    }

    // La baja pide confirmación: primero se elige el recinto y después se confirma
    fun pedirBaja(recintoId: Long) = formulario.update { it.copy(confirmandoBajaId = recintoId, error = null) }
    fun cancelarBaja() = formulario.update { it.copy(confirmandoBajaId = null) }

    fun confirmarBaja() {
        val recintoId = formulario.value.confirmandoBajaId ?: return
        ejecutar("Recinto dado de baja") {
            repositorio.darDeBaja(recintoId)
            formulario.update { it.copy(confirmandoBajaId = null) }
        }
    }

    private fun ejecutar(aviso: String, accion: suspend () -> Unit) {
        viewModelScope.launch {
            formulario.update { it.copy(guardando = true, error = null) }
            try {
                accion()
                formulario.update { it.copy(guardando = false, mensaje = aviso) }
            } catch (e: Exception) {
                formulario.update { it.copy(guardando = false, error = mensajeDeError(e), confirmandoBajaId = null) }
            }
        }
    }
}
