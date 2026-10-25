package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EventoDetalle
import cl.duoc.aurasacrapp.repository.EventoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AgendaUiState(
    val cargando: Boolean = true,
    val eventos: List<EventoDetalle> = emptyList()
)

class AgendaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = EventoRepository(AppDatabase.getDatabase(application).eventoDao())

    val uiState: StateFlow<AgendaUiState> = repositorio.proximos()
        .map { eventos -> AgendaUiState(cargando = false, eventos = eventos) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AgendaUiState()
        )
}
