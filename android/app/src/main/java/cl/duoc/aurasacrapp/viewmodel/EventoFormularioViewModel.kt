package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.TipoEvento
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.EncargadoRepository
import cl.duoc.aurasacrapp.repository.EventoRepository
import cl.duoc.aurasacrapp.util.millisAFechaTexto
import cl.duoc.aurasacrapp.util.millisAHoraTexto
import cl.duoc.aurasacrapp.util.parsearFecha
import cl.duoc.aurasacrapp.util.parsearHora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

// Estado del formulario de publicar o editar un evento
data class EventoFormUiState(
    val cargando: Boolean = false,                 // true mientras se carga el evento que se edita
    val tipo: TipoEvento = TipoEvento.CONCIERTO,
    val titulo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val horaInicio: String = "",
    val fechaFin: String = "",
    val horaFin: String = "",
    val errores: Map<String, String> = emptyMap(), // un mensaje por campo
    val guardando: Boolean = false,
    val error: String? = null,                     // error del servidor
    val guardado: Boolean = false
) {
    // Las aperturas y los cierres excepcionales exigen hora de término
    val exigeTermino: Boolean
        get() = tipo == TipoEvento.APERTURA_ESPECIAL || tipo == TipoEvento.CIERRE_ESPECIAL
}

// Sirve para publicar (sin argumento) y para editar (la ruta "editar-evento/{eventoId}" trae el id)
class EventoFormularioViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val eventoId: Long? = savedStateHandle.get<Long>("eventoId")
    val esEdicion: Boolean = eventoId != null

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = EncargadoRepository(db.recintoDao(), EventoRepository(db.eventoDao()))

    private val _uiState = MutableStateFlow(EventoFormUiState(cargando = eventoId != null))
    val uiState: StateFlow<EventoFormUiState> = _uiState.asStateFlow()

    init {
        if (eventoId != null) cargarEvento(eventoId)
    }

    // Al editar, el formulario parte con los datos actuales del evento
    private fun cargarEvento(id: Long) {
        viewModelScope.launch {
            try {
                val evento = repositorio.eventos().firstOrNull { it.id == id }
                if (evento == null) {
                    _uiState.update { it.copy(cargando = false, error = "No encontramos este evento.") }
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        cargando = false,
                        tipo = evento.tipo,
                        titulo = evento.titulo,
                        descripcion = evento.descripcion.orEmpty(),
                        fechaInicio = millisAFechaTexto(evento.inicio),
                        horaInicio = millisAHoraTexto(evento.inicio),
                        fechaFin = evento.fin?.let { f -> millisAFechaTexto(f) }.orEmpty(),
                        horaFin = evento.fin?.let { f -> millisAHoraTexto(f) }.orEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun onTipoChange(tipo: TipoEvento) = _uiState.update { it.copy(tipo = tipo, errores = it.errores - "fin") }
    fun onTituloChange(valor: String) = _uiState.update { it.copy(titulo = valor, errores = it.errores - "titulo") }
    fun onDescripcionChange(valor: String) = _uiState.update { it.copy(descripcion = valor, errores = it.errores - "descripcion") }
    fun onFechaInicioChange(valor: String) = _uiState.update { it.copy(fechaInicio = valor, errores = it.errores - "inicio") }
    fun onHoraInicioChange(valor: String) = _uiState.update { it.copy(horaInicio = valor, errores = it.errores - "inicio") }
    fun onFechaFinChange(valor: String) = _uiState.update { it.copy(fechaFin = valor, errores = it.errores - "fin") }
    fun onHoraFinChange(valor: String) = _uiState.update { it.copy(horaFin = valor, errores = it.errores - "fin") }

    fun guardar() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()

        if (estado.titulo.trim().isEmpty() || estado.titulo.trim().length > MAX_TITULO) {
            errores["titulo"] = "El título es obligatorio (máximo $MAX_TITULO caracteres)"
        }
        if (estado.descripcion.trim().length > MAX_DESCRIPCION) {
            errores["descripcion"] = "Máximo $MAX_DESCRIPCION caracteres"
        }

        val fechaInicio = parsearFecha(estado.fechaInicio)
        val horaInicio = parsearHora(estado.horaInicio)
        val inicio = if (fechaInicio != null && horaInicio != null) LocalDateTime.of(fechaInicio, horaInicio) else null
        if (inicio == null) errores["inicio"] = "Indica la fecha (dd/mm/aaaa) y la hora (hh:mm) de inicio"

        // El término es opcional, salvo en aperturas y cierres excepcionales
        var fin: LocalDateTime? = null
        val hayTermino = estado.fechaFin.isNotBlank() || estado.horaFin.isNotBlank()
        if (hayTermino) {
            val fechaFin = parsearFecha(estado.fechaFin)
            val horaFin = parsearHora(estado.horaFin)
            if (fechaFin == null || horaFin == null) {
                errores["fin"] = "Indica la fecha (dd/mm/aaaa) y la hora (hh:mm) de término"
            } else {
                fin = LocalDateTime.of(fechaFin, horaFin)
                if (inicio != null && !fin.isAfter(inicio)) errores["fin"] = "El término debe ser posterior al inicio"
            }
        } else if (estado.exigeTermino) {
            errores["fin"] = "Las aperturas y los cierres excepcionales requieren la hora de término"
        }

        if (errores.isNotEmpty() || inicio == null) {
            _uiState.update { it.copy(errores = errores, error = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            try {
                val descripcion = estado.descripcion.ifBlank { null }
                if (eventoId == null) {
                    repositorio.publicarEvento(estado.tipo, estado.titulo, descripcion, inicio, fin)
                } else {
                    repositorio.editarEvento(eventoId, estado.tipo, estado.titulo, descripcion, inicio, fin)
                }
                _uiState.update { it.copy(guardando = false, guardado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    private companion object {
        const val MAX_TITULO = 150
        const val MAX_DESCRIPCION = 2000
    }
}
