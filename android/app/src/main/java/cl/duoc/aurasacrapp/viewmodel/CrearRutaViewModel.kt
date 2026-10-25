package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.model.ParadaRuta
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Ruta
import cl.duoc.aurasacrapp.repository.RecintoRepository
import cl.duoc.aurasacrapp.util.CalculadoraTiempos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CrearRutaUiState(
    val recintos: List<RecintoConComuna> = emptyList(),
    val seleccionados: List<Long> = emptyList(),      // ids en el orden en que se eligieron
    val modo: ModoTraslado = ModoTraslado.A_PIE,
    val minutosTotales: Int = 0
)

class CrearRutaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = RecintoRepository(AppDatabase.getDatabase(application).recintoDao())
    private val seleccionados = MutableStateFlow<List<Long>>(emptyList())
    private val modo = MutableStateFlow(ModoTraslado.A_PIE)

    // Cada vez que cambia la selección o el modo, se recalcula el tiempo estimado
    val uiState: StateFlow<CrearRutaUiState> = combine(
        repositorio.recintos,
        seleccionados,
        modo
    ) { recintos, ids, modoActual ->
        val paradas = ids.mapIndexedNotNull { i, id ->
            recintos.find { it.id == id }?.let { r ->
                ParadaRuta(i + 1, r.id, r.nombreOficial, r.comuna, r.latitud, r.longitud, tiempoVisitaMin = 30)
            }
        }
        val ruta = Ruta(id = 0, nombre = "Mi ruta", descripcion = "", modoTraslado = modoActual, paradas = paradas)
        CrearRutaUiState(
            recintos = recintos,
            seleccionados = ids,
            modo = modoActual,
            minutosTotales = if (paradas.isEmpty()) 0 else CalculadoraTiempos.minutosTotales(ruta)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CrearRutaUiState()
    )

    // Marca o desmarca un recinto como parada
    fun alternar(recintoId: Long) {
        val actuales = seleccionados.value
        seleccionados.value = if (recintoId in actuales) actuales - recintoId else actuales + recintoId
    }

    fun elegirModo(nuevoModo: ModoTraslado) {
        modo.value = nuevoModo
    }
}
