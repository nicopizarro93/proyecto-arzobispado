package cl.duoc.santiago_sacro_api.recinto.dto;

import cl.duoc.santiago_sacro_api.recinto.TipoRecinto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RecintoRequestTest {

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void crearValidador() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        fabrica.close();
    }

    private Set<String> camposConError(String latitud, String longitud) {
        RecintoRequest solicitud = new RecintoRequest(TipoRecinto.TEMPLO, "Parroquia de Prueba", 1L,
                "Calle Prueba", "100", new BigDecimal(latitud), new BigDecimal(longitud), null);
        return validador.validate(solicitud).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void conCoordenadasValidasNoTieneErrores() {
        assertTrue(camposConError("-33.4378", "-70.6505").isEmpty());
    }

    @Test
    void unaLatitudFueraDeRangoEsInvalida() {
        assertTrue(camposConError("95", "-70.6505").contains("latitud"));
    }

    @Test
    void unaLongitudFueraDeRangoEsInvalida() {
        assertTrue(camposConError("-33.4378", "-181").contains("longitud"));
    }

    @Test
    void limpiaEspaciosYDejaLosCamposVaciosEnNull() {
        RecintoRequest r = new RecintoRequest(TipoRecinto.MUSEO, "  Museo Nuevo  ", 1L, " Calle 1 ", "   ",
                new BigDecimal("-33.4"), new BigDecimal("-70.6"), "  ");
        assertEquals("Museo Nuevo", r.nombre());
        assertEquals("Calle 1", r.calle());
        assertNull(r.numero());
        assertNull(r.descripcionHistorica());
    }
}
