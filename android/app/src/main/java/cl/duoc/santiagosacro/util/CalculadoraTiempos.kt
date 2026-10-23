package cl.duoc.santiagosacro.util

import cl.duoc.santiagosacro.model.Ruta
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Estima el tiempo de una ruta con la misma fórmula del backend (CalculadoraTiempos.java)
object CalculadoraTiempos {

    private const val FACTOR_DESVIO = 1.3        // las calles no van en línea recta: +30 %
    private const val RADIO_TIERRA_KM = 6371.0

    // Minutos de traslado entre cada parada y la siguiente (se redondea hacia arriba)
    fun minutosTraslado(ruta: Ruta): List<Int> =
        ruta.paradas.zipWithNext { desde, hasta ->
            val km = distanciaKm(desde.latitud, desde.longitud, hasta.latitud, hasta.longitud) * FACTOR_DESVIO
            ceil(km / ruta.modoTraslado.velocidadKmh * 60).toInt()
        }

    fun minutosVisita(ruta: Ruta): Int = ruta.paradas.sumOf { it.tiempoVisitaMin }

    fun minutosTotales(ruta: Ruta): Int = minutosVisita(ruta) + minutosTraslado(ruta).sum()

    // Fórmula de Haversine: distancia en línea recta sobre la superficie de la Tierra
    fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val p1 = Math.toRadians(lat1)
        val p2 = Math.toRadians(lat2)
        val dLat = p2 - p1
        val dLon = Math.toRadians(lon2 - lon1)
        val h = sin(dLat / 2).pow(2) + cos(p1) * cos(p2) * sin(dLon / 2).pow(2)
        return 2 * RADIO_TIERRA_KM * asin(sqrt(h))
    }
}
