package cl.duoc.santiago_sacro_api.visita.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VisitaRequestTest {

    private static final LocalDateTime EN_UNA_SEMANA = LocalDateTime.now().plusDays(7);

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

    private Set<String> camposConError(VisitaRequest solicitud) {
        return validador.validate(solicitud).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void unaVisitaAUnRecintoEsValida() {
        assertTrue(camposConError(new VisitaRequest(3L, null, EN_UNA_SEMANA, 4, null, null)).isEmpty());
    }

    @Test
    void recintoYRutaALaVezEsInvalido() {
        assertTrue(camposConError(new VisitaRequest(3L, 1L, EN_UNA_SEMANA, 4, null, null))
                .contains("destinoUnico"));
    }

    @Test
    void sinDestinoEsInvalido() {
        assertTrue(camposConError(new VisitaRequest(null, null, EN_UNA_SEMANA, 4, null, null))
                .contains("destinoUnico"));
    }

    @Test
    void fechaPasadaYCeroPersonasSonInvalidas() {
        Set<String> errores = camposConError(
                new VisitaRequest(3L, null, LocalDateTime.now().minusDays(1), 0, null, null));
        assertTrue(errores.contains("inicio"));
        assertTrue(errores.contains("cantidadPersonas"));
    }
}