package cl.duoc.aurasacrapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.model.RutaDetalle
import cl.duoc.aurasacrapp.model.RutaResumen
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.RutaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RutasUiState(
    val cargando: Boolean = true,
    val sugeridas: List<RutaResumen> = emptyList(),
    val mias: List<RutaResumen> = emptyList(),
    val error: String? = null
)

class RutasViewModel(
    private val repositorio: RutaRepository = RutaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RutasUiState())
    val uiState: StateFlow<RutasUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    // Pide las rutas al backend. La pantalla lo vuelve a llamar al regresar de crear una ruta.
    fun cargar() {
        viewModelScope.launch {
            try {
                val sugeridas = repositorio.sugeridas()
                val mias = repositorio.mias()
                _uiState.update { it.copy(cargando = false, sugeridas = sugeridas, mias = mias, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }
}

data class RutaDetalleUiState(
    val cargando: Boolean = true,
    val detalle: RutaDetalle? = null,    // null y sin cargar = no se encontró la ruta
    val error: String? = null
)

// SavedStateHandle trae el argumento de la ruta: "ruta/{rutaId}".
// @JvmOverloads genera también el constructor de un solo parámetro (SavedStateHandle),
// que es el que Android busca para crear este ViewModel.
class RutaDetalleViewModel @JvmOverloads constructor(
    savedStateHandle: SavedStateHandle,
    private val repositorio: RutaRepository = RutaRepository()
) : ViewModel() {

    private val rutaId: Long = savedStateHandle.get<Long>("rutaId") ?: 0L

    private val _uiState = MutableStateFlow(RutaDetalleUiState())
    val uiState: StateFlow<RutaDetalleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val detalle = repositorio.detalle(rutaId)
                _uiState.update { it.copy(cargando = false, detalle = detalle) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }
}
