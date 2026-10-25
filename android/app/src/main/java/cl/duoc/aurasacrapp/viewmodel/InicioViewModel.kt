package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.TipoRecinto
import cl.duoc.aurasacrapp.repository.RecintoRepository
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.util.sinAcentos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Estado de la pantalla Inicio
data class InicioUiState(
    val nombreUsuario: String = "",
    val tipoSeleccionado: TipoRecinto? = null,          // null = todos los tipos
    val recintos: List<RecintoConComuna> = emptyList(),
    val recintoACargo: RecintoConComuna? = null,       // solo para encargados
    val cargando: Boolean = true
)

class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = RecintoRepository(AppDatabase.getDatabase(application).recintoDao())

    // Lo que el usuario escribe en el buscador (se expone aparte para que el campo responda al instante)
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val tipoSeleccionado = MutableStateFlow<TipoRecinto?>(null)

    // combine: cada vez que cambia la lista, la búsqueda, el filtro o la sesión, se recalcula el estado
    val uiState: StateFlow<InicioUiState> = combine(
        repositorio.recintos,
        _busqueda,
        tipoSeleccionado,
        SesionRepository.usuario
    ) { recintos, texto, tipo, usuario ->
        // Se compara sin tildes y sin mayúsculas: "maipu" encuentra "Maipú"
        val filtro = texto.trim().sinAcentos()
        val filtrados = recintos.filter { r ->
            val coincideTipo = tipo == null || r.tipoRecinto == tipo
            val coincideTexto = filtro.isEmpty() ||
                r.nombreOficial.sinAcentos().contains(filtro, ignoreCase = true) ||
                r.comuna.sinAcentos().contains(filtro, ignoreCase = true)
            coincideTipo && coincideTexto
        }
        // Si quien inició sesión es encargado, se busca su recinto para mostrarle el acceso a "Mi recinto"
        val recintoACargo = if (usuario?.rol == Rol.ENCARGADO) {
            recintos.find { it.id == usuario?.recintoId }
        } else {
            null
        }
        InicioUiState(
            nombreUsuario = usuario?.nombre ?: "",
            tipoSeleccionado = tipo,
            recintos = filtrados,
            recintoACargo = recintoACargo,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InicioUiState()
    )

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun onTipoSeleccionado(tipo: TipoRecinto?) {
        tipoSeleccionado.value = tipo
    }
}
