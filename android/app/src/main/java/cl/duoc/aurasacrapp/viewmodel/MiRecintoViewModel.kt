package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.repository.RecintoRepository
import cl.duoc.aurasacrapp.repository.SesionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MiRecintoUiState(
    val cargando: Boolean = true,
    val recinto: RecintoConComuna? = null,       // null = la cuenta no tiene recinto a cargo
    val estadoElegido: EstadoApertura? = null,   // la opción marcada en pantalla
    val guardando: Boolean = false,
    val mensaje: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class MiRecintoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = RecintoRepository(AppDatabase.getDatabase(application).recintoDao())

    private val estadoElegido = MutableStateFlow<EstadoApertura?>(null)
    private val guardando = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    // El recinto del encargado que inició sesión (solo si su rol es ENCARGADO)
    private val recintoACargo: Flow<RecintoConComuna?> = SesionRepository.usuario
        .flatMapLatest { usuario ->
            val recintoId = usuario?.recintoId
            if (usuario?.rol == Rol.ENCARGADO && recintoId != null) {
                repositorio.recinto(recintoId)
            } else {
                flowOf(null)
            }
        }

    val uiState: StateFlow<MiRecintoUiState> = combine(
        recintoACargo,
        estadoElegido,
        guardando,
        mensaje
    ) { recinto, elegido, guardandoAhora, texto ->
        MiRecintoUiState(
            cargando = false,
            recinto = recinto,
            estadoElegido = elegido ?: recinto?.estadoApertura,   // si no ha elegido, se marca el actual
            guardando = guardandoAhora,
            mensaje = texto
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MiRecintoUiState()
    )

    fun elegirEstado(estado: EstadoApertura) {
        estadoElegido.value = estado
    }

    fun guardar() {
        val recinto = uiState.value.recinto ?: return
        val nuevoEstado = estadoElegido.value ?: return
        viewModelScope.launch {
            guardando.value = true
            repositorio.cambiarEstado(recinto.id, nuevoEstado)
            guardando.value = false
            estadoElegido.value = null
            mensaje.value = "Estado actualizado"
        }
    }

    // La pantalla avisa que ya mostró el mensaje, para no repetirlo
    fun mensajeMostrado() {
        mensaje.value = null
    }
}
