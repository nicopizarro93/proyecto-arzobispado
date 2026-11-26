package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EventoGestion
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.EncargadoRepository
import cl.duoc.aurasacrapp.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisEventosUiState(
    val cargando: Boolean = true,
    val eventos: List<EventoGestion> = emptyList(),
    val mensaje: String? = null,      // aviso corto, por ejemplo "Evento cancelado"
    val error: String? = null
)

// Eventos, avisos, aperturas y cierres que publicó el encargado (con los cancelados)
class MisEventosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = EncargadoRepository(db.recintoDao(), EventoRepository(db.eventoDao()))

    private val _uiState = MutableStateFlow(MisEventosUiState())
    val uiState: StateFlow<MisEventosUiState> = _uiState.asStateFlow()

    // La pantalla lo llama cada vez que se muestra (por ejemplo, al volver de publicar un evento)
    fun cargar() {
        viewModelScope.launch {
            try {
                val eventos = repositorio.eventos()
                _uiState.update { it.copy(cargando = false, eventos = eventos, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun cancelar(eventoId: Long) {
        viewModelScope.launch {
            try {
                repositorio.cancelarEvento(eventoId)
                _uiState.update { it.copy(eventos = repositorio.eventos(), mensaje = "Evento cancelado", error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = mensajeDeError(e)) }
            }
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}
