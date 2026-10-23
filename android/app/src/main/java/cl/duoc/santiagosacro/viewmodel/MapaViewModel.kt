package cl.duoc.santiagosacro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.santiagosacro.database.AppDatabase
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.repository.RecintoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MapaUiState(
    val recintos: List<RecintoConComuna> = emptyList(),
    val seleccionado: RecintoConComuna? = null
)

class MapaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = RecintoRepository(AppDatabase.getDatabase(application).recintoDao())
    private val seleccionadoId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<MapaUiState> = combine(repositorio.recintos, seleccionadoId) { recintos, id ->
        MapaUiState(recintos = recintos, seleccionado = recintos.find { it.id == id })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapaUiState()
    )

    fun seleccionar(recintoId: Long?) {
        seleccionadoId.value = recintoId
    }
}
