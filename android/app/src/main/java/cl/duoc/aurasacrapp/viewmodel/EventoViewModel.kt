package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EventoDetalle
import cl.duoc.aurasacrapp.repository.EventoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class EventoUiState(
    val cargando: Boolean = true,
    val evento: EventoDetalle? = null
)

// SavedStateHandle trae el argumento de la ruta: "evento/{eventoId}"
class EventoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val eventoId: Long = savedStateHandle.get<Long>("eventoId") ?: 0L
    private val repositorio = EventoRepository(AppDatabase.getDatabase(application).eventoDao())

    val uiState: StateFlow<EventoUiState> = repositorio.evento(eventoId)
        .map { evento -> EventoUiState(cargando = false, evento = evento) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EventoUiState()
        )
}
