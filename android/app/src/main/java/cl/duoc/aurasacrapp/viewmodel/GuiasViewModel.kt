package cl.duoc.aurasacrapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.model.Guia
import cl.duoc.aurasacrapp.model.SolicitudRecinto
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.GuiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla Guías y solicitudes del encargado
data class GuiasUiState(
    val cargando: Boolean = true,
    val guias: List<Guia> = emptyList(),
    val solicitudes: List<SolicitudRecinto> = emptyList(),
    // Formulario de un guía nuevo
    val nombre: String = "",
    val apellido: String = "",
    val erroresNuevo: Map<String, String> = emptyMap(),
    // Asignar: guías sugeridos para la solicitud elegida (los disponibles en ese horario)
    val asignandoId: Long? = null,
    val sugeridos: List<Guia> = emptyList(),
    // Rechazar: la solicitud elegida y el motivo
    val rechazandoId: Long? = null,
    val motivo: String = "",
    val errorMotivo: String? = null,
    val guardando: Boolean = false,
    val mensaje: String? = null,                 // aviso corto de lo que se hizo
    val error: String? = null                    // error del servidor
)

class GuiasViewModel(
    private val repositorio: GuiaRepository = GuiaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuiasUiState())
    val uiState: StateFlow<GuiasUiState> = _uiState.asStateFlow()

    // La pantalla lo llama cada vez que se muestra (por ejemplo, al volver de la disponibilidad de un guía)
    fun cargar() {
        viewModelScope.launch {
            try {
                val guias = repositorio.guias()
                val solicitudes = repositorio.solicitudesDelRecinto()
                _uiState.update { it.copy(cargando = false, guias = guias, solicitudes = solicitudes, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun onNombreChange(valor: String) = _uiState.update { it.copy(nombre = valor, erroresNuevo = it.erroresNuevo - "nombre") }
    fun onApellidoChange(valor: String) = _uiState.update { it.copy(apellido = valor, erroresNuevo = it.erroresNuevo - "apellido") }
    fun onMotivoChange(valor: String) = _uiState.update { it.copy(motivo = valor, errorMotivo = null) }
    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun crearGuia() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()
        if (estado.nombre.trim().length !in 2..50) errores["nombre"] = "El nombre debe tener entre 2 y 50 caracteres"
        if (estado.apellido.trim().length !in 2..50) errores["apellido"] = "El apellido debe tener entre 2 y 50 caracteres"
        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(erroresNuevo = errores) }
            return
        }
        ejecutar("Guía agregado") {
            repositorio.crearGuia(estado.nombre, estado.apellido)
            _uiState.update { it.copy(nombre = "", apellido = "") }
        }
    }

    // Un guía desactivado no se puede asignar, pero queda en el historial
    fun cambiarActivo(guia: Guia) {
        ejecutar(if (guia.activo) "Guía desactivado" else "Guía activado") {
            repositorio.cambiarActivo(guia.id, !guia.activo)
        }
    }

    // Pide al backend los guías disponibles en el horario de la solicitud
    fun abrirAsignar(solicitudId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(rechazandoId = null, error = null) }
            try {
                val sugeridos = repositorio.guiasDisponibles(solicitudId)
                _uiState.update { it.copy(asignandoId = solicitudId, sugeridos = sugeridos) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = mensajeDeError(e)) }
            }
        }
    }

    fun asignar(solicitudId: Long, guiaId: Long) {
        ejecutar("Guía asignado") {
            repositorio.asignar(solicitudId, guiaId)
            _uiState.update { it.copy(asignandoId = null, sugeridos = emptyList()) }
        }
    }

    fun abrirRechazo(solicitudId: Long) =
        _uiState.update { it.copy(rechazandoId = solicitudId, asignandoId = null, motivo = "", errorMotivo = null) }

    fun cancelarAccion() =
        _uiState.update { it.copy(rechazandoId = null, asignandoId = null, sugeridos = emptyList(), motivo = "") }

    // Para rechazar hay que escribir el motivo
    fun confirmarRechazo() {
        val estado = _uiState.value
        val solicitudId = estado.rechazandoId ?: return
        if (estado.motivo.isBlank() || estado.motivo.trim().length > 300) {
            _uiState.update { it.copy(errorMotivo = "Escribe el motivo del rechazo (máximo 300 caracteres)") }
            return
        }
        ejecutar("Solicitud rechazada") {
            repositorio.rechazar(solicitudId, estado.motivo)
            _uiState.update { it.copy(rechazandoId = null, motivo = "") }
        }
    }

    // Ejecuta un cambio en el backend, vuelve a pedir las listas y avisa el resultado
    private fun ejecutar(aviso: String, accion: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            try {
                accion()
                val guias = repositorio.guias()
                val solicitudes = repositorio.solicitudesDelRecinto()
                _uiState.update { it.copy(guardando = false, guias = guias, solicitudes = solicitudes, mensaje = aviso) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }
}
