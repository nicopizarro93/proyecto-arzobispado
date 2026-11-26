package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Horario
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.EncargadoRepository
import cl.duoc.aurasacrapp.repository.EventoRepository
import cl.duoc.aurasacrapp.util.parsearHora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla Editar ficha: la reseña y los horarios del recinto
data class EditarFichaUiState(
    val cargando: Boolean = true,
    val recintoId: Long? = null,
    val historia: String = "",
    val horarios: List<Horario> = emptyList(),
    // Formulario del bloque de horario (nuevo o en edición)
    val dia: Int = 1,                          // 1 = lunes ... 7 = domingo
    val apertura: String = "",
    val cierre: String = "",
    val editandoHorarioId: Long? = null,       // null = se está agregando un bloque nuevo
    val errores: Map<String, String> = emptyMap(),
    val guardando: Boolean = false,
    val mensaje: String? = null,               // aviso corto de lo que se guardó
    val error: String? = null                  // error del servidor
)

class EditarFichaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = EncargadoRepository(db.recintoDao(), EventoRepository(db.eventoDao()))

    private val _uiState = MutableStateFlow(EditarFichaUiState())
    val uiState: StateFlow<EditarFichaUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            try {
                val ficha = repositorio.ficha()
                _uiState.update {
                    it.copy(cargando = false, recintoId = ficha.recintoId, historia = ficha.historia, horarios = ficha.horarios)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun onHistoriaChange(valor: String) = _uiState.update { it.copy(historia = valor, errores = it.errores - "historia") }
    fun onDiaChange(dia: Int) = _uiState.update { it.copy(dia = dia) }
    fun onAperturaChange(valor: String) = _uiState.update { it.copy(apertura = valor, errores = it.errores - "apertura") }
    fun onCierreChange(valor: String) = _uiState.update { it.copy(cierre = valor, errores = it.errores - "cierre") }
    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun guardarHistoria() {
        val estado = _uiState.value
        val recintoId = estado.recintoId ?: return
        if (estado.historia.trim().length > MAX_HISTORIA) {
            _uiState.update { it.copy(errores = it.errores + ("historia" to "Máximo $MAX_HISTORIA caracteres")) }
            return
        }
        ejecutar("Reseña guardada") { repositorio.guardarHistoria(recintoId, estado.historia) }
    }

    // Pasa un bloque existente al formulario para editarlo
    fun editarHorario(horario: Horario) = _uiState.update {
        it.copy(
            dia = horario.diaSemana, apertura = horario.apertura, cierre = horario.cierre,
            editandoHorarioId = horario.id, errores = emptyMap(), error = null
        )
    }

    fun cancelarEdicionHorario() = _uiState.update {
        it.copy(apertura = "", cierre = "", editandoHorarioId = null, errores = emptyMap())
    }

    fun guardarHorario() {
        val estado = _uiState.value
        val errores = mutableMapOf<String, String>()
        val apertura = parsearHora(estado.apertura)
        val cierre = parsearHora(estado.cierre)
        if (apertura == null) errores["apertura"] = "Usa el formato hh:mm"
        if (cierre == null) errores["cierre"] = "Usa el formato hh:mm"
        if (apertura != null && cierre != null && !cierre.isAfter(apertura)) {
            errores["cierre"] = "El cierre debe ser posterior a la apertura"
        }
        if (errores.isNotEmpty() || apertura == null || cierre == null) {
            _uiState.update { it.copy(errores = errores, error = null) }
            return
        }
        ejecutar(if (estado.editandoHorarioId == null) "Horario agregado" else "Horario actualizado") {
            repositorio.guardarHorario(estado.editandoHorarioId, estado.dia, apertura, cierre)
            _uiState.update { it.copy(apertura = "", cierre = "", editandoHorarioId = null) }
        }
    }

    fun eliminarHorario(horarioId: Long) {
        ejecutar("Horario eliminado") {
            repositorio.eliminarHorario(horarioId)
            if (_uiState.value.editandoHorarioId == horarioId) {
                _uiState.update { it.copy(apertura = "", cierre = "", editandoHorarioId = null) }
            }
        }
    }

    // Ejecuta un cambio en el backend, vuelve a pedir la ficha y avisa el resultado
    private fun ejecutar(aviso: String, accion: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            try {
                accion()
                val ficha = repositorio.ficha()
                _uiState.update { it.copy(guardando = false, horarios = ficha.horarios, mensaje = aviso) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    private companion object {
        const val MAX_HISTORIA = 5000
    }
}
