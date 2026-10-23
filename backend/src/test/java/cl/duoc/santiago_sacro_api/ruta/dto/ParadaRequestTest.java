package cl.duoc.santiago_sacro_api.ruta.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParadaRequestTest {

    @Test
    void sinMinutosUsaLos30PorDefecto() {
        assertEquals(30, new ParadaRequest(1L, null).tiempoVisitaMin());
    }

    @Test
    void detectaRecintosRepetidos() {
        assertTrue(ParadaRequest.sinRecintosRepetidos(
                List.of(new ParadaRequest(1L, 30), new ParadaRequest(2L, 30))));
        assertFalse(ParadaRequest.sinRecintosRepetidos(
                List.of(new ParadaRequest(1L, 30), new ParadaRequest(1L, 45))));
    }
}
