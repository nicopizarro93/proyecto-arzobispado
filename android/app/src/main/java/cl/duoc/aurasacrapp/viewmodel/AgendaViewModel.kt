package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EventoDetalle
import cl.duoc.aurasacrapp.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import cl.duoc.aurasacrapp.remote.mensajeDeError
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class AgendaUiState(
    val cargando: Boolean = true,
    val eventos: List<EventoDetalle> = emptyList(),
    val error: String? = null          // aviso si no se pudo actualizar desde el backend
)

class AgendaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = EventoRepository(AppDatabase.getDatabase(application).eventoDao())

    private val error = MutableStateFlow<String?>(null)

    init {
        actualizar()
    }

    // Baja los eventos del backend; si falla, se siguen mostrando los guardados
    fun actualizar() {
        viewModelScope.launch {
            try {
                repositorio.sincronizar()
                error.value = null
            } catch (e: Exception) {
                error.value = mensajeDeError(e) + ". Se muestran los datos guardados."
            }
        }
    }

    val uiState: StateFlow<AgendaUiState> = combine(repositorio.proximos(), error) { eventos, mensaje ->
        AgendaUiState(cargando = false, eventos = eventos, error = mensaje)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AgendaUiState()
    )
}
