package cl.duoc.santiagosacro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.santiagosacro.database.AppDatabase
import cl.duoc.santiagosacro.model.EventoDetalle
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.repository.EventoRepository
import cl.duoc.santiagosacro.repository.RecintoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FichaUiState(
    val cargando: Boolean = true,
    val recinto: RecintoConComuna? = null,
    val eventos: List<EventoDetalle> = emptyList()
)

// SavedStateHandle trae los argumentos de la ruta: "ficha/{recintoId}"
class FichaViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recintoId: Long = savedStateHandle.get<Long>("recintoId") ?: 0L
    private val db = AppDatabase.getDatabase(application)
    private val repositorio = RecintoRepository(db.recintoDao())
    private val eventoRepositorio = EventoRepository(db.eventoDao())

    // Junta el recinto y sus próximos eventos en un solo estado
    val uiState: StateFlow<FichaUiState> = combine(
        repositorio.recinto(recintoId),
        eventoRepositorio.proximosDe(recintoId)
    ) { recinto, eventos ->
        FichaUiState(cargando = false, recinto = recinto, eventos = eventos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FichaUiState()
    )
}
