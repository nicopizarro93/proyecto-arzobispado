package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.VisitaAgendada
import cl.duoc.aurasacrapp.model.VisitaDetalle
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.repository.VisitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VisitasUiState(
    val cargando: Boolean = true,
    val escaneadas: List<VisitaDetalle> = emptyList(),   // QR escaneados (Room)
    val agendadas: List<VisitaAgendada> = emptyList(),   // visitas planificadas (backend)
    val mensaje: String? = null,
    val puedePlanificar: Boolean = false   // solo visitantes y docentes tienen visitas planificadas
)

@OptIn(ExperimentalCoroutinesApi::class)
class VisitasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = VisitaRepository(db.visitaDao(), db.recintoDao())

    private val agendadas = MutableStateFlow<List<VisitaAgendada>>(emptyList())
    private val cargando = MutableStateFlow(true)
    private val mensaje = MutableStateFlow<String?>(null)

    // flatMapLatest: si cambia el usuario de la sesión, se cambia la consulta por la de ese usuario
    private val escaneadas = SesionRepository.usuario.flatMapLatest { usuario ->
        if (usuario == null) flowOf(emptyList()) else repositorio.visitasDe(usuario.id)
    }

    val uiState: StateFlow<VisitasUiState> = combine(
        escaneadas, agendadas, cargando, mensaje, SesionRepository.usuario
    ) { escaneadasAhora, agendadasAhora, cargandoAhora, texto, usuario ->
        VisitasUiState(
            cargando = cargandoAhora,
            escaneadas = escaneadasAhora,
            agendadas = agendadasAhora,
            mensaje = texto,
            puedePlanificar = puedePlanificar(usuario?.rol)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VisitasUiState()
    )

    // Pide las visitas planificadas al backend (la pantalla lo llama cada vez que se muestra)
    fun cargar() {
        // El backend rechaza (403) a encargados y administradores: no se le pregunta
        if (!puedePlanificar(SesionRepository.usuario.value?.rol)) {
            cargando.value = false
            return
        }
        viewModelScope.launch {
            try {
                agendadas.value = repositorio.agendadas()
                mensaje.value = null
            } catch (e: Exception) {
                mensaje.value = mensajeDeError(e)
            }
            cargando.value = false
        }
    }

    fun cancelar(visitaId: Long) {
        viewModelScope.launch {
            try {
                repositorio.cancelar(visitaId)
                agendadas.value = repositorio.agendadas()
                mensaje.value = null
            } catch (e: Exception) {
                mensaje.value = mensajeDeError(e)
            }
        }
    }

    private fun puedePlanificar(rol: Rol?) = rol == Rol.VISITANTE || rol == Rol.DOCENTE
}
