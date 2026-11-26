package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.repository.VisitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Estado del formulario de planificar visita
data class PlanificarUiState(
    val fecha: String = "",
    val hora: String = "",
    val personas: Int = 1,
    val curso: String = "",
    val observaciones: String = "",
    val errores: Map<String, String> = emptyMap(),   // un mensaje por campo
    val guardando: Boolean = false,
    val error: String? = null,                       // error del servidor
    val visitaAgendada: Boolean = false,
    val esDocente: Boolean = false                   // el docente debe indicar el curso
)

// La ruta "planificar/{recintoId}" o "planificar-ruta/{rutaId}" trae el destino;
// SavedStateHandle lo entrega al ViewModel (uno de los dos ids existe, el otro es null)
class PlanificarVisitaViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recintoId: Long? = savedStateHandle.get<Long>("recintoId")
    private val rutaId: Long? = savedStateHandle.get<Long>("rutaId")
    private val db = AppDatabase.getDatabase(application)
    private val repositorio = VisitaRepository(db.visitaDao(), db.recintoDao())

    private val _uiState = MutableStateFlow(
        PlanificarUiState(esDocente = SesionRepository.usuario.value?.rol == Rol.DOCENTE)
    )
    val uiState: StateFlow<PlanificarUiState> = _uiState.asStateFlow()

    fun onFechaChange(valor: String) = _uiState.update { it.copy(fecha = valor, errores = it.errores - "fecha") }
    fun onHoraChange(valor: String) = _uiState.update { it.copy(hora = valor, errores = it.errores - "hora") }
    fun onCursoChange(valor: String) = _uiState.update { it.copy(curso = valor, errores = it.errores - "curso") }
    fun onObservacionesChange(valor: String) =
        _uiState.update { it.copy(observaciones = valor, errores = it.errores - "observaciones") }

    fun onPersonasChange(valor: Int) = _uiState.update { it.copy(personas = valor.coerceIn(1, MAX_PERSONAS)) }

    fun agendar() {
        val estado = _uiState.value
        val esDocente = estado.esDocente
        val errores = mutableMapOf<String, String>()

        val fecha = try {
            LocalDate.parse(estado.fecha.trim(), FORMATO_FECHA)
        } catch (e: DateTimeParseException) {
            errores["fecha"] = "Usa el formato dd/mm/aaaa"
            null
        }
        val hora = try {
            LocalTime.parse(estado.hora.trim(), FORMATO_HORA)
        } catch (e: DateTimeParseException) {
            errores["hora"] = "Usa el formato hh:mm"
            null
        }
        val inicio = if (fecha != null && hora != null) LocalDateTime.of(fecha, hora) else null
        if (inicio != null && !inicio.isAfter(LocalDateTime.now())) {
            errores["fecha"] = "La fecha y hora deben ser futuras"
        }
        val curso = estado.curso.trim()
        if (esDocente && curso.length !in 2..50) {
            errores["curso"] = "Indica el nivel o curso (máximo 50 caracteres)"
        }
        if (estado.observaciones.trim().length > MAX_OBSERVACIONES) {
            errores["observaciones"] = "Máximo $MAX_OBSERVACIONES caracteres"
        }
        if (errores.isNotEmpty() || inicio == null) {
            _uiState.update { it.copy(errores = errores, error = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            try {
                repositorio.agendar(
                    recintoId = recintoId,
                    rutaId = rutaId,
                    inicio = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(inicio),
                    cantidadPersonas = estado.personas,
                    nivelCurso = if (esDocente) curso else null,
                    observaciones = estado.observaciones.trim().ifEmpty { null }
                )
                _uiState.update { it.copy(guardando = false, visitaAgendada = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    private companion object {
        const val MAX_PERSONAS = 200
        const val MAX_OBSERVACIONES = 500
        val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/uuuu")
        val FORMATO_HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    }
}
