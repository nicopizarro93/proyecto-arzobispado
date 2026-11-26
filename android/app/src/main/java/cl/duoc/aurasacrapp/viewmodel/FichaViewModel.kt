package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EventoDetalle
import cl.duoc.aurasacrapp.model.Horario
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.repository.EventoRepository
import cl.duoc.aurasacrapp.repository.FavoritoRepository
import cl.duoc.aurasacrapp.repository.RecintoRepository
import cl.duoc.aurasacrapp.repository.SesionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FichaUiState(
    val cargando: Boolean = true,
    val recinto: RecintoConComuna? = null,
    val eventos: List<EventoDetalle> = emptyList(),
    val horarios: List<Horario> = emptyList(),
    val esDocente: Boolean = false,       // el docente ve campos extra al planificar la visita
    val puedePlanificar: Boolean = false, // solo visitantes y docentes agendan visitas
    val esFavorito: Boolean = false       // ¿el usuario marcó este recinto como favorito?
)

// Se usa en la Ficha y en Planificar visita: las dos rutas traen el argumento {recintoId}
@OptIn(ExperimentalCoroutinesApi::class)
class FichaViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recintoId: Long = savedStateHandle.get<Long>("recintoId") ?: 0L
    private val db = AppDatabase.getDatabase(application)
    private val repositorio = RecintoRepository(db.recintoDao(), db.comunaDao())
    private val eventoRepositorio = EventoRepository(db.eventoDao())
    private val favoritoRepositorio = FavoritoRepository(db.favoritoDao())

    // Horario de la semana: viene del backend junto con la historia del recinto
    private val horarios = MutableStateFlow<List<Horario>>(emptyList())

    init {
        viewModelScope.launch {
            try {
                horarios.value = repositorio.cargarFicha(recintoId)
            } catch (e: Exception) {
                // sin conexión: se muestran los datos guardados en Room
            }
        }
    }

    // ¿Es favorito? Se lee de Room, así que funciona sin conexión y se actualiza solo al marcarlo
    private val esFavorito = SesionRepository.usuario.flatMapLatest { usuario ->
        if (usuario == null) flowOf(false) else favoritoRepositorio.ids(usuario.id).map { recintoId in it }
    }

    // Marca el recinto como favorito o lo desmarca
    fun alternarFavorito() {
        val usuario = SesionRepository.usuario.value ?: return
        viewModelScope.launch { favoritoRepositorio.alternar(usuario.id, recintoId) }
    }

    // Junta el recinto, sus próximos eventos, el horario, si es favorito y la sesión en un solo estado
    val uiState: StateFlow<FichaUiState> = combine(
        repositorio.recinto(recintoId),
        eventoRepositorio.proximosDe(recintoId),
        horarios,
        esFavorito,
        SesionRepository.usuario
    ) { recinto, eventos, horariosSemana, favorito, usuario ->
        FichaUiState(
            cargando = false,
            recinto = recinto,
            eventos = eventos,
            horarios = horariosSemana,
            esDocente = usuario?.rol == Rol.DOCENTE,
            puedePlanificar = usuario?.rol == Rol.VISITANTE || usuario?.rol == Rol.DOCENTE,
            esFavorito = favorito
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FichaUiState()
    )
}
