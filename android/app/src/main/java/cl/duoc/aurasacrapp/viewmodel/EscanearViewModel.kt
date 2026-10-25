package cl.duoc.aurasacrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aurasacrapp.database.AppDatabase
import cl.duoc.aurasacrapp.repository.SesionRepository
import cl.duoc.aurasacrapp.repository.VisitaRepository
import cl.duoc.aurasacrapp.util.CodigoQr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EscanearUiState(
    val procesando: Boolean = false,
    val error: String? = null,
    val recintoRegistrado: Long? = null   // cuando tiene valor, la pantalla abre la ficha
)

class EscanearViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repositorio = VisitaRepository(db.visitaDao(), db.recintoDao())

    private val _uiState = MutableStateFlow(EscanearUiState())
    val uiState: StateFlow<EscanearUiState> = _uiState.asStateFlow()

    // Recibe el texto leído por ML Kit
    fun procesarCodigo(texto: String?) {
        // 1. ¿Es un código de AuraSacrapp?
        val recintoId = CodigoQr.leerRecintoId(texto)
        if (recintoId == null) {
            _uiState.update { it.copy(error = "Este código QR no pertenece a AuraSacrapp.") }
            return
        }
        // 2. ¿Hay alguien con sesión iniciada?
        val usuario = SesionRepository.usuario.value
        if (usuario == null) {
            _uiState.update { it.copy(error = "Inicia sesión para registrar tus visitas.") }
            return
        }
        // 3. Registra la visita en Room
        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true, error = null) }
            val recinto = repositorio.registrarVisita(usuario.id, recintoId)
            _uiState.update {
                if (recinto != null) {
                    it.copy(procesando = false, recintoRegistrado = recinto.id)
                } else {
                    it.copy(procesando = false, error = "El recinto de este código no está disponible.")
                }
            }
        }
    }

    fun onErrorEscaner(mensaje: String) {
        _uiState.update { it.copy(error = mensaje) }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }

    // La pantalla avisa que ya navegó, para no volver a abrir la ficha al regresar
    fun navegacionRealizada() {
        _uiState.update { it.copy(recintoRegistrado = null) }
    }
}
