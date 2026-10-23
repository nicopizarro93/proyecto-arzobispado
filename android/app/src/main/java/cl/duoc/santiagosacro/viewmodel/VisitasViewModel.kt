package cl.duoc.santiagosacro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.santiagosacro.database.AppDatabase
import cl.duoc.santiagosacro.model.VisitaDetalle
import cl.duoc.santiagosacro.repository.SesionRepository
import cl.duoc.santiagosacro.repository.VisitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class VisitasUiState(
    val cargando: Boolean = true,
    val visitas: List<VisitaDetalle> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class VisitasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = VisitaRepository(db.visitaDao(), db.recintoDao())

    // flatMapLatest: si cambia el usuario de la sesión, se cambia la consulta por la de ese usuario
    val uiState: StateFlow<VisitasUiState> = SesionRepository.usuario
        .flatMapLatest { usuario ->
            if (usuario == null) flowOf(emptyList()) else repositorio.visitasDe(usuario.id)
        }
        .map { visitas -> VisitasUiState(cargando = false, visitas = visitas) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VisitasUiState()
        )
}
