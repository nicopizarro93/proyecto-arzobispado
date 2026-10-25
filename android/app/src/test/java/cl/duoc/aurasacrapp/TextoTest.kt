package cl.duoc.aurasacrapp

import cl.duoc.aurasacrapp.util.sinAcentos
import org.junit.Assert.assertEquals
import org.junit.Test

// Pruebas unitarias de la búsqueda sin tildes
class TextoTest {

    @Test
    fun quitaLasTildes() {
        assertEquals("Maipu", "Maipú".sinAcentos())
    }

    @Test
    fun quitaLaTildeDeLaEnie() {
        assertEquals("Nunoa", "Ñuñoa".sinAcentos())
    }

    @Test
    fun noCambiaUnTextoSinTildes() {
        assertEquals("Recoleta", "Recoleta".sinAcentos())
    }
}
