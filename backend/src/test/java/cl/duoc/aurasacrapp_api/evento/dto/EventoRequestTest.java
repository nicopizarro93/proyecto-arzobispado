package cl.duoc.aurasacrapp_api.evento.dto;

import cl.duoc.aurasacrapp_api.evento.TipoEvento;
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

class EventoRequestTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 5, 19, 0);

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

    private Set<String> camposConError(EventoRequest solicitud) {
        return validador.validate(solicitud).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void eventoSinTerminoEsValido() {
        assertTrue(camposConError(new EventoRequest(TipoEvento.CONCIERTO, "Concierto", null, INICIO, null)).isEmpty());
    }

    @Test
    void terminoAntesDelInicioEsInvalido() {
        assertTrue(camposConError(new EventoRequest(TipoEvento.AVISO, "Aviso", null, INICIO, INICIO.minusHours(2)))
                .contains("finPosteriorAlInicio"));
    }

    @Test
    void cierreSinTerminoEsInvalido() {
        assertTrue(camposConError(new EventoRequest(TipoEvento.CIERRE_ESPECIAL, "Reparaciones", null, INICIO, null))
                .contains("finPresenteEnExcepciones"));
    }

    @Test
    void tituloEnBlancoEsInvalido() {
        assertTrue(camposConError(new EventoRequest(TipoEvento.CONCIERTO, "   ", null, INICIO, null))
                .contains("titulo"));
    }

    @Test
    void limpiaEspaciosYDejaLaDescripcionVaciaEnNull() {
        EventoRequest r = new EventoRequest(TipoEvento.CONCIERTO, "  Concierto  ", "   ", INICIO, null);
        assertEquals("Concierto", r.titulo());
        assertNull(r.descripcion());
    }
}
