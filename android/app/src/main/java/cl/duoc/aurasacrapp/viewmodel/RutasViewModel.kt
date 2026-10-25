package cl.duoc.aurasacrapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cl.duoc.aurasacrapp.model.Ruta
import cl.duoc.aurasacrapp.repository.RutaRepository
import cl.duoc.aurasacrapp.util.CalculadoraTiempos

// Una ruta con su tiempo total ya calculado, para la lista
data class RutaResumen(val ruta: Ruta, val minutosTotales: Int)

class RutasViewModel : ViewModel() {
    val rutas: List<RutaResumen> = RutaRepository.rutas().map { ruta ->
        RutaResumen(ruta, CalculadoraTiempos.minutosTotales(ruta))
    }
}

// Detalle de una ruta: paradas, minutos de cada traslado y totales
data class RutaDetalle(
    val ruta: Ruta,
    val traslados: List<Int>,
    val minutosVisita: Int,
    val minutosTraslado: Int,
    val minutosTotales: Int
)

// SavedStateHandle trae el argumento de la ruta: "ruta/{rutaId}"
class RutaDetalleViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val rutaId: Long = savedStateHandle.get<Long>("rutaId") ?: 0L

    val detalle: RutaDetalle? = RutaRepository.ruta(rutaId)?.let { ruta ->
        val traslados = CalculadoraTiempos.minutosTraslado(ruta)
        RutaDetalle(
            ruta = ruta,
            traslados = traslados,
            minutosVisita = CalculadoraTiempos.minutosVisita(ruta),
            minutosTraslado = traslados.sum(),
            minutosTotales = CalculadoraTiempos.minutosTotales(ruta)
        )
    }
}
