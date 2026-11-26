package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.SolicitudUsuario
import cl.duoc.aurasacrapp.model.VisitaAgendada
import cl.duoc.aurasacrapp.model.VisitaDetalle
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.GuiaRepository
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VisitasUiState(
    val cargando: Boolean = true,
    val escaneadas: List<VisitaDetalle> = emptyList(),      // QR escaneados (Room)
    val agendadas: List<VisitaAgendada> = emptyList(),      // visitas planificadas (backend)
    val solicitudes: List<SolicitudUsuario> = emptyList(),  // solicitudes de guía de esas visitas (backend)
    val mensaje: String? = null,
    val puedePlanificar: Boolean = false   // solo visitantes y docentes tienen visitas planificadas
)

// Lo que se pide al backend: las visitas planificadas y las solicitudes de guía
private data class DatosDelBackend(
    val agendadas: List<VisitaAgendada> = emptyList(),
    val solicitudes: List<SolicitudUsuario> = emptyList(),
    val cargando: Boolean = true,
    val mensaje: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class VisitasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = VisitaRepository(db.visitaDao(), db.recintoDao())
    private val guiaRepositorio = GuiaRepository()

    private val backend = MutableStateFlow(DatosDelBackend())

    // flatMapLatest: si cambia el usuario de la sesión, se cambia la consulta por la de ese usuario
    private val escaneadas = SesionRepository.usuario.flatMapLatest { usuario ->
        if (usuario == null) flowOf(emptyList()) else repositorio.visitasDe(usuario.id)
    }

    val uiState: StateFlow<VisitasUiState> = combine(
        escaneadas, backend, SesionRepository.usuario
    ) { escaneadasAhora, datos, usuario ->
        VisitasUiState(
            cargando = datos.cargando,
            escaneadas = escaneadasAhora,
            agendadas = datos.agendadas,
            solicitudes = datos.solicitudes,
            mensaje = datos.mensaje,
            puedePlanificar = puedePlanificar(usuario?.rol)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VisitasUiState()
    )

    // Pide al backend las visitas planificadas y sus solicitudes de guía
    // (la pantalla lo llama cada vez que se muestra)
    fun cargar() {
        // El backend rechaza (403) a encargados y administradores: no se les pregunta
        if (!puedePlanificar(SesionRepository.usuario.value?.rol)) {
            backend.update { it.copy(cargando = false) }
            return
        }
        viewModelScope.launch {
            try {
                val agendadas = repositorio.agendadas()
                val solicitudes = guiaRepositorio.misSolicitudes()
                backend.value = DatosDelBackend(agendadas, solicitudes, cargando = false)
            } catch (e: Exception) {
                backend.update { it.copy(cargando = false, mensaje = mensajeDeError(e)) }
            }
        }
    }

    fun cancelar(visitaId: Long) {
        viewModelScope.launch {
            try {
                repositorio.cancelar(visitaId)
                cargar()
            } catch (e: Exception) {
                backend.update { it.copy(mensaje = mensajeDeError(e)) }
            }
        }
    }

    private fun puedePlanificar(rol: Rol?) = rol == Rol.VISITANTE || rol == Rol.DOCENTE
}
