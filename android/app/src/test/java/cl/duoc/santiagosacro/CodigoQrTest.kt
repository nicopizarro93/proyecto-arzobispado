package cl.duoc.santiagosacro

import cl.duoc.santiagosacro.util.CodigoQr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas unitarias del lector de códigos QR de la app (corren en el PC, sin emulador)
class CodigoQrTest {

    @Test
    fun leeElIdDeUnCodigoValido() {
        assertEquals(1L, CodigoQr.leerRecintoId("santiagosacro:recinto:1"))
    }

    @Test
    fun ignoraEspaciosYMayusculas() {
        assertEquals(7L, CodigoQr.leerRecintoId("  SantiagoSacro:Recinto:7 "))
    }

    @Test
    fun rechazaCodigosDeOtrasApps() {
        assertNull(CodigoQr.leerRecintoId("https://www.duoc.cl"))
    }

    @Test
    fun rechazaIdNoNumericoOCero() {
        assertNull(CodigoQr.leerRecintoId("santiagosacro:recinto:abc"))
        assertNull(CodigoQr.leerRecintoId("santiagosacro:recinto:0"))
    }

    @Test
    fun rechazaCodigoNulo() {
        assertNull(CodigoQr.leerRecintoId(null))
    }

    @Test
    fun crearYLeerDevuelvenElMismoId() {
        assertEquals(5L, CodigoQr.leerRecintoId(CodigoQr.crear(5)))
    }
}
