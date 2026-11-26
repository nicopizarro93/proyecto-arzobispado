package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.TipoRecinto
import cl.duoc.aurasacrapp.remote.mensajeDeError
import cl.duoc.aurasacrapp.repository.EventoRepository
import cl.duoc.aurasacrapp.repository.FavoritoRepository
import cl.duoc.aurasacrapp.repository.RecintoRepository
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.util.sinAcentos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Filtros de la lista de recintos; se pueden combinar (null / false = sin filtrar por ese criterio)
data class FiltrosInicio(
    val tipo: TipoRecinto? = null,
    val comuna: String? = null,
    val estado: EstadoApertura? = null,
    val soloFavoritos: Boolean = false
) {
    val hayFiltros: Boolean get() = tipo != null || comuna != null || estado != null || soloFavoritos
}

// Estado de la pantalla Inicio
data class InicioUiState(
    val nombreUsuario: String = "",
    val filtros: FiltrosInicio = FiltrosInicio(),
    val hayFiltros: Boolean = false,                    // filtros o búsqueda activos (para ofrecer limpiarlos)
    val comunas: List<String> = emptyList(),            // comunas que tienen recintos, para el filtro
    val recintos: List<RecintoConComuna> = emptyList(),
    val recintoACargo: RecintoConComuna? = null,       // solo para encargados
    val cargando: Boolean = true,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = RecintoRepository(db.recintoDao(), db.comunaDao())
    private val eventoRepositorio = EventoRepository(db.eventoDao())
    private val favoritoRepositorio = FavoritoRepository(db.favoritoDao())

    // Aviso si no se pudo actualizar desde el backend
    private val error = MutableStateFlow<String?>(null)

    init {
        actualizar()
    }

    // Baja recintos y eventos del backend y los guarda en Room (los recintos van primero:
    // cada evento apunta a su recinto). Si falla, se siguen mostrando los datos guardados.
    fun actualizar() {
        viewModelScope.launch {
            try {
                repositorio.sincronizar()
                eventoRepositorio.sincronizar()
                error.value = null
            } catch (e: Exception) {
                error.value = mensajeDeError(e) + ". Se muestran los datos guardados."
            }
        }
    }

    // Lo que el usuario escribe en el buscador (se expone aparte para que el campo responda al instante)
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val filtros = MutableStateFlow(FiltrosInicio())

    // Ids de los recintos favoritos del usuario (viven en Room: funcionan sin conexión)
    private val favoritos = SesionRepository.usuario.flatMapLatest { usuario ->
        if (usuario == null) flowOf(emptySet()) else favoritoRepositorio.ids(usuario.id)
    }

    private val recintosYFiltros = combine(repositorio.recintos, _busqueda, filtros) { recintos, texto, f ->
        Triple(recintos, texto, f)
    }

    // combine: cada vez que cambia la lista, la búsqueda, los filtros, los favoritos o la sesión,
    // se recalcula el estado
    val uiState: StateFlow<InicioUiState> = combine(
        recintosYFiltros,
        SesionRepository.usuario,
        error,
        favoritos
    ) { base, usuario, mensajeError, idsFavoritos ->
        val (recintos, texto, f) = base
        // Se compara sin tildes y sin mayúsculas: "maipu" encuentra "Maipú"
        val buscado = texto.trim().sinAcentos()
        val filtrados = recintos.filter { r ->
            val coincideTexto = buscado.isEmpty() ||
                r.nombreOficial.sinAcentos().contains(buscado, ignoreCase = true) ||
                r.comuna.sinAcentos().contains(buscado, ignoreCase = true)
            coincideTexto &&
                (f.tipo == null || r.tipoRecinto == f.tipo) &&
                (f.comuna == null || r.comuna == f.comuna) &&
                (f.estado == null || r.estadoApertura == f.estado) &&
                (!f.soloFavoritos || r.id in idsFavoritos)
        }
        // Si quien inició sesión es encargado, se busca su recinto para mostrarle el acceso a "Mi recinto"
        val recintoACargo = if (usuario?.rol == Rol.ENCARGADO) {
            recintos.find { it.id == usuario.recintoId }
        } else {
            null
        }
        InicioUiState(
            nombreUsuario = usuario?.nombre ?: "",
            filtros = f,
            hayFiltros = f.hayFiltros || texto.isNotBlank(),
            comunas = recintos.map { it.comuna }.distinct().sorted(),
            recintos = filtrados,
            recintoACargo = recintoACargo,
            cargando = false,
            error = mensajeError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InicioUiState()
    )

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun onTipoSeleccionado(tipo: TipoRecinto?) = filtros.update { it.copy(tipo = tipo) }

    fun onComunaSeleccionada(comuna: String?) = filtros.update { it.copy(comuna = comuna) }

    // Tocar el estado que ya estaba elegido lo quita
    fun onEstadoSeleccionado(estado: EstadoApertura?) =
        filtros.update { it.copy(estado = if (it.estado == estado) null else estado) }

    fun onSoloFavoritos(activo: Boolean) = filtros.update { it.copy(soloFavoritos = activo) }

    // Deja la lista completa: sin búsqueda y sin filtros
    fun limpiarFiltros() {
        _busqueda.value = ""
        filtros.value = FiltrosInicio()
    }
}
