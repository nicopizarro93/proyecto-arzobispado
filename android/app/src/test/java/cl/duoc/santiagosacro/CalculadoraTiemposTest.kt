package cl.duoc.santiagosacro

import cl.duoc.santiagosacro.model.ModoTraslado
import cl.duoc.santiagosacro.model.ParadaRuta
import cl.duoc.santiagosacro.model.Ruta
import cl.duoc.santiagosacro.util.CalculadoraTiempos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas unitarias del cálculo de tiempos de una ruta (misma fórmula que el backend)
class CalculadoraTiemposTest {

    private val capilla = ParadaRuta(1, 1, "Capilla del Buen Pastor", "Maipú", -33.509700, -70.757400, 30)
    private val museo = ParadaRuta(2, 3, "Museo de Arte Sacro Los Aromos", "Maipú", -33.512600, -70.750100, 60)

    @Test
    fun unGradoDeLongitudEnElEcuadorMideCasi111Km() {
        assertEquals(111.19, CalculadoraTiempos.distanciaKm(0.0, 0.0, 0.0, 1.0), 0.01)
    }

    @Test
    fun sinDistanciaSoloSumaLosMinutosDeVisita() {
        val ruta = Ruta(1, "Prueba", "", ModoTraslado.A_PIE, listOf(capilla, capilla.copy(orden = 2)))
        assertEquals(60, CalculadoraTiempos.minutosTotales(ruta))
    }

    @Test
    fun aPieSeDemoraMasQueEnVehiculo() {
        val aPie = Ruta(1, "A pie", "", ModoTraslado.A_PIE, listOf(capilla, museo))
        val enAuto = aPie.copy(modoTraslado = ModoTraslado.VEHICULO)
        assertTrue(CalculadoraTiempos.minutosTraslado(aPie).sum() > CalculadoraTiempos.minutosTraslado(enAuto).sum())
    }
}
