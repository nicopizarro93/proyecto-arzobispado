package cl.duoc.aurasacrapp

import cl.duoc.aurasacrapp.util.CodigoQr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas unitarias del lector de códigos QR de la app (corren en el PC, sin emulador)
class CodigoQrTest {

    @Test
    fun leeElIdDeUnCodigoValido() {
        assertEquals(1L, CodigoQr.leerRecintoId("aurasacrapp:recinto:1"))
    }

    @Test
    fun ignoraEspaciosYMayusculas() {
        assertEquals(7L, CodigoQr.leerRecintoId("  AuraSacrapp:Recinto:7 "))
    }

    @Test
    fun rechazaCodigosDeOtrasApps() {
        assertNull(CodigoQr.leerRecintoId("https://www.duoc.cl"))
    }

    @Test
    fun rechazaIdNoNumericoOCero() {
        assertNull(CodigoQr.leerRecintoId("aurasacrapp:recinto:abc"))
        assertNull(CodigoQr.leerRecintoId("aurasacrapp:recinto:0"))
    }

    @Test
    fun rechazaCodigoNulo() {
        assertNull(CodigoQr.leerRecintoId(null))
    }
}
