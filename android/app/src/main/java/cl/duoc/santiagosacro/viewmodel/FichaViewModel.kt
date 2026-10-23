package cl.duoc.santiagosacro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.santiagosacro.database.AppDatabase
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.repository.RecintoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FichaUiState(
    val cargando: Boolean = true,
    val recinto: RecintoConComuna? = null
)

// SavedStateHandle trae los argumentos de la ruta: "ficha/{recintoId}"
class FichaViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recintoId: Long = savedStateHandle.get<Long>("recintoId") ?: 0L
    private val repositorio = RecintoRepository(AppDatabase.getDatabase(application).recintoDao())

    val uiState: StateFlow<FichaUiState> = repositorio.recinto(recintoId)
        .map { recinto -> FichaUiState(cargando = false, recinto = recinto) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FichaUiState()
        )
}
