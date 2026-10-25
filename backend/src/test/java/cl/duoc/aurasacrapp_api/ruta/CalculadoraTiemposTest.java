package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.ruta.dto.EstimacionDto;
import cl.duoc.aurasacrapp_api.ruta.dto.TramoDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraTiemposTest {

    private final CalculadoraTiempos calculadora = new CalculadoraTiempos();

    // Coordenadas de la "Ruta del Llano" (datos de prueba V3).
    private static final PuntoRuta CAPILLA = punto(1L, "-33.509700", "-70.757400", 30);
    private static final PuntoRuta SAN_LUCAS = punto(2L, "-33.511200", "-70.752900", 30);
    private static final PuntoRuta MUSEO = punto(3L, "-33.512600", "-70.750100", 60);

    private static PuntoRuta punto(Long id, String latitud, String longitud, int minutosVisita) {
        return new PuntoRuta(id, new BigDecimal(latitud), new BigDecimal(longitud), minutosVisita);
    }

    @Test
    void unGradoDeLatitudSonUnos111Kilometros() {
        double km = CalculadoraTiempos.distanciaKm(punto(1L, "0", "0", 0), punto(2L, "1", "0", 0));
        assertEquals(111.19, km, 0.01);
    }

    @Test
    void laRutaDelLlanoAPieSumaVisitasYTraslados() {
        EstimacionDto e = calculadora.estimar(ModoTraslado.A_PIE, List.of(CAPILLA, SAN_LUCAS, MUSEO));

        assertEquals(120, e.minutosVisita());
        assertEquals(14, e.minutosTraslado());
        assertEquals(134, e.minutosTotal());
        assertEquals(0.97, e.distanciaKm(), 0.001);
        assertEquals(List.of(8, 6), e.tramos().stream().map(TramoDto::minutos).toList());
    }

    @Test
    void elModoSoloCambiaElTiempoDeTraslado() {
        EstimacionDto e = calculadora.estimar(ModoTraslado.VEHICULO, List.of(CAPILLA, SAN_LUCAS, MUSEO));

        assertEquals(120, e.minutosVisita());
        assertEquals(2, e.minutosTraslado());
        assertEquals(0.97, e.distanciaKm(), 0.001);
    }

    @Test
    void unaSolaParadaNoTieneTraslados() {
        EstimacionDto e = calculadora.estimar(ModoTraslado.A_PIE, List.of(MUSEO));

        assertTrue(e.tramos().isEmpty());
        assertEquals(60, e.minutosTotal());
    }

    @Test
    void unTramoMuyCortoCuentaAlMenosUnMinuto() {
        PuntoRuta a = punto(1L, "-33.500000", "-70.700000", 30);
        PuntoRuta b = punto(2L, "-33.500100", "-70.700000", 30); // unos 11 metros al sur

        EstimacionDto e = calculadora.estimar(ModoTraslado.VEHICULO, List.of(a, b));

        assertEquals(1, e.tramos().get(0).minutos());
    }
}
