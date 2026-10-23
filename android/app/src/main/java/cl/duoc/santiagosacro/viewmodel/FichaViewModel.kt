package cl.duoc.santiagosacro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.santiagosacro.database.AppDatabase
import cl.duoc.santiagosacro.model.EventoDetalle
import cl.duoc.santiagosacro.model.Guia
import cl.duoc.santiagosacro.model.Horario
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.model.Rol
import cl.duoc.santiagosacro.repository.EventoRepository
import cl.duoc.santiagosacro.repository.GuiaRepository
import cl.duoc.santiagosacro.repository.HorarioRepository
import cl.duoc.santiagosacro.repository.RecintoRepository
import cl.duoc.santiagosacro.repository.SesionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FichaUiState(
    val cargando: Boolean = true,
    val recinto: RecintoConComuna? = null,
    val eventos: List<EventoDetalle> = emptyList(),
    val horarios: List<Horario> = emptyList(),
    val guias: List<Guia> = emptyList(),
    val esDocente: Boolean = false        // el docente ve campos extra al planificar la visita
)

// Se usa en la Ficha y en Planificar visita: las dos rutas traen el argumento {recintoId}
class FichaViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recintoId: Long = savedStateHandle.get<Long>("recintoId") ?: 0L
    private val db = AppDatabase.getDatabase(application)
    private val repositorio = RecintoRepository(db.recintoDao())
    private val eventoRepositorio = EventoRepository(db.eventoDao())

    // Junta el recinto, sus próximos eventos y la sesión en un solo estado
    val uiState: StateFlow<FichaUiState> = combine(
        repositorio.recinto(recintoId),
        eventoRepositorio.proximosDe(recintoId),
        SesionRepository.usuario
    ) { recinto, eventos, usuario ->
        FichaUiState(
            cargando = false,
            recinto = recinto,
            eventos = eventos,
            horarios = HorarioRepository.horariosDe(recintoId),
            guias = GuiaRepository.guiasDe(recintoId),
            esDocente = usuario?.rol == Rol.DOCENTE
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FichaUiState()
    )
}
