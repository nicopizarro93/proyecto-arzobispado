package cl.duoc.aurasacrapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.model.Guia
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.GuiaRepository
import cl.duoc.aurasacrapp.util.parsearFecha
import cl.duoc.aurasacrapp.util.parsearHora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

// Estado de la pantalla de disponibilidad de un guía
data class DisponibilidadUiState(
    val cargando: Boolean = true,
    val guia: Guia? = null,                    // null y sin cargar = no se encontró el guía
    val fechaInicio: String = "",
    val horaInicio: String = "",
    val fechaFin: String = "",
    val horaFin: String = "",
    val errores: Map<String, String> = emptyMap(),
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

// La ruta "guia/{guiaId}" trae el id del guía. @JvmOverloads genera el constructor
// (SavedStateHandle) que Android busca para crear este ViewModel.
class GuiaDisponibilidadViewModel @JvmOverloads constructor(
    savedStateHandle: SavedStateHandle,
    private val repositorio: GuiaRepository = GuiaRepository()
) : ViewModel() {

    private val guiaId: Long = savedStateHandle.get<Long>("guiaId") ?: 0L

    private val _uiState = MutableStateFlow(DisponibilidadUiState())
    val uiState: StateFlow<DisponibilidadUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val guia = repositorio.guias().firstOrNull { it.id == guiaId }
                _uiState.update { it.copy(cargando = false, guia = guia) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun onFechaInicioChange(v: String) = _uiState.update { it.copy(fechaInicio = v, errores = it.errores - "inicio") }
    fun onHoraInicioChange(v: String) = _uiState.update { it.copy(horaInicio = v, errores = it.errores - "inicio") }
    fun onFechaFinChange(v: String) = _uiState.update { it.copy(fechaFin = v, errores = it.errores - "fin") }
    fun onHoraFinChange(v: String) = _uiState.update { it.copy(horaFin = v, errores = it.errores - "fin") }
    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun agregar() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()
        val fechaInicio = parsearFecha(estado.fechaInicio)
        val horaInicio = parsearHora(estado.horaInicio)
        val fechaFin = parsearFecha(estado.fechaFin)
        val horaFin = parsearHora(estado.horaFin)
        val inicio = if (fechaInicio != null && horaInicio != null) LocalDateTime.of(fechaInicio, horaInicio) else null
        val fin = if (fechaFin != null && horaFin != null) LocalDateTime.of(fechaFin, horaFin) else null
        if (inicio == null) errores["inicio"] = "Indica la fecha (dd/mm/aaaa) y la hora (hh:mm) de inicio"
        if (fin == null) {
            errores["fin"] = "Indica la fecha (dd/mm/aaaa) y la hora (hh:mm) de término"
        } else if (inicio != null && !fin.isAfter(inicio)) {
            errores["fin"] = "El término debe ser posterior al inicio"
        }
        if (errores.isNotEmpty() || inicio == null || fin == null) {
            _uiState.update { it.copy(errores = errores, error = null) }
            return
        }
        ejecutar("Disponibilidad agregada") {
            val guia = repositorio.agregarDisponibilidad(guiaId, inicio, fin)
            _uiState.update { it.copy(guia = guia, horaInicio = "", horaFin = "", fechaInicio = "", fechaFin = "") }
        }
    }

    fun eliminar(bloqueId: Long) {
        ejecutar("Disponibilidad eliminada") {
            val guia = repositorio.eliminarDisponibilidad(guiaId, bloqueId)
            _uiState.update { it.copy(guia = guia) }
        }
    }

    private fun ejecutar(aviso: String, accion: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            try {
                accion()
                _uiState.update { it.copy(guardando = false, mensaje = aviso) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }
}
