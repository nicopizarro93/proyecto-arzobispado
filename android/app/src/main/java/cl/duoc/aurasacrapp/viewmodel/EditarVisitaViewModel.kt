package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.VisitaRepository
import cl.duoc.aurasacrapp.util.millisAFechaTexto
import cl.duoc.aurasacrapp.util.millisAHoraTexto
import cl.duoc.aurasacrapp.util.parsearFecha
import cl.duoc.aurasacrapp.util.parsearHora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Estado de la pantalla Editar visita: solo se cambian la fecha, la hora y la cantidad de personas
data class EditarVisitaUiState(
    val cargando: Boolean = true,
    val destino: String = "",                  // recinto o ruta de la visita (no se cambia)
    val fecha: String = "",
    val hora: String = "",
    val personas: Int = 1,
    val errores: Map<String, String> = emptyMap(),
    val guardando: Boolean = false,
    val error: String? = null,                 // error del servidor
    val guardado: Boolean = false
)

// La ruta "editar-visita/{visitaId}" trae el id de la visita; SavedStateHandle lo entrega al ViewModel
class EditarVisitaViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val visitaId: Long = savedStateHandle.get<Long>("visitaId") ?: 0L
    private val db = AppDatabase.getDatabase(application)
    private val repositorio = VisitaRepository(db.visitaDao(), db.recintoDao())

    private val _uiState = MutableStateFlow(EditarVisitaUiState())
    val uiState: StateFlow<EditarVisitaUiState> = _uiState.asStateFlow()

    // El formulario parte con los datos actuales de la visita
    init {
        viewModelScope.launch {
            try {
                val visita = repositorio.agendadas().firstOrNull { it.id == visitaId }
                if (visita == null) {
                    _uiState.update { it.copy(cargando = false, error = "No encontramos esta visita.") }
                } else {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            destino = visita.destino,
                            fecha = millisAFechaTexto(visita.inicio),
                            hora = millisAHoraTexto(visita.inicio),
                            personas = visita.cantidadPersonas
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun onFechaChange(valor: String) = _uiState.update { it.copy(fecha = valor, errores = it.errores - "fecha") }
    fun onHoraChange(valor: String) = _uiState.update { it.copy(hora = valor, errores = it.errores - "hora") }
    fun onPersonasChange(valor: Int) = _uiState.update { it.copy(personas = valor.coerceIn(1, MAX_PERSONAS)) }

    fun guardar() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()
        val fecha = parsearFecha(estado.fecha)
        val hora = parsearHora(estado.hora)
        if (fecha == null) errores["fecha"] = "Usa el formato dd/mm/aaaa"
        if (hora == null) errores["hora"] = "Usa el formato hh:mm"
        val inicio = if (fecha != null && hora != null) LocalDateTime.of(fecha, hora) else null
        if (inicio != null && !inicio.isAfter(LocalDateTime.now())) {
            errores["fecha"] = "La fecha y hora deben ser futuras"
        }
        if (errores.isNotEmpty() || inicio == null) {
            _uiState.update { it.copy(errores = errores, error = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            try {
                repositorio.cambiar(visitaId, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(inicio), estado.personas)
                _uiState.update { it.copy(guardando = false, guardado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    private companion object {
        const val MAX_PERSONAS = 200
    }
}
