package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.RecintoRepository
import cl.duoc.aurasacrapp.repository.RutaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CrearRutaUiState(
    val nombre: String = "",
    val modo: ModoTraslado = ModoTraslado.A_PIE,
    val recintos: List<RecintoConComuna> = emptyList(),
    val seleccionados: List<Long> = emptyList(),   // ids en el orden en que se eligieron
    val guardando: Boolean = false,
    val error: String? = null,
    val rutaGuardada: Boolean = false
) {
    // Una ruta necesita nombre y al menos 2 recintos
    val puedeGuardar: Boolean get() = nombre.isNotBlank() && seleccionados.size >= 2 && !guardando
}

class CrearRutaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val recintoRepositorio = RecintoRepository(db.recintoDao(), db.comunaDao())
    private val rutaRepositorio = RutaRepository()

    // Lo que el usuario va completando en el formulario
    private val formulario = MutableStateFlow(CrearRutaUiState())

    val uiState: StateFlow<CrearRutaUiState> = combine(recintoRepositorio.recintos, formulario) { recintos, estado ->
        estado.copy(recintos = recintos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CrearRutaUiState()
    )

    fun onNombreChange(valor: String) = formulario.update { it.copy(nombre = valor, error = null) }

    fun onModoChange(modo: ModoTraslado) = formulario.update { it.copy(modo = modo) }

    // Marca o desmarca un recinto; las paradas quedan en el orden en que se marcaron
    fun alternarRecinto(recintoId: Long) = formulario.update {
        val nuevos = if (recintoId in it.seleccionados) it.seleccionados - recintoId else it.seleccionados + recintoId
        it.copy(seleccionados = nuevos, error = null)
    }

    fun guardar() {
        val estado = formulario.value
        if (!estado.puedeGuardar) return
        viewModelScope.launch {
            formulario.update { it.copy(guardando = true, error = null) }
            try {
                rutaRepositorio.crear(estado.nombre, estado.modo, estado.seleccionados)
                formulario.update { it.copy(guardando = false, rutaGuardada = true) }
            } catch (e: Exception) {
                formulario.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }
}
