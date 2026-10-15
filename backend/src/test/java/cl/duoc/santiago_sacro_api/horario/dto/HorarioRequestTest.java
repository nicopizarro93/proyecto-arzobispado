package cl.duoc.santiago_sacro_api.horario.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HorarioRequestTest {

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

    private Set<String> camposConError(Integer dia, String apertura, String cierre) {
        HorarioRequest solicitud = new HorarioRequest(dia,
                apertura == null ? null : LocalTime.parse(apertura),
                cierre == null ? null : LocalTime.parse(cierre));
        return validador.validate(solicitud).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void bloqueValidoNoTieneErrores() {
        assertTrue(camposConError(1, "09:00", "13:00").isEmpty());
    }

    @Test
    void cierreAntesDeLaAperturaEsInvalido() {
        assertTrue(camposConError(2, "18:00", "10:00").contains("rangoValido"));
    }

    @Test
    void diaFueraDeRangoEsInvalido() {
        assertTrue(camposConError(8, "09:00", "13:00").contains("diaSemana"));
    }

    @Test
    void sinHorasSoloAvisaLosCamposObligatorios() {
        Set<String> errores = camposConError(1, null, null);
        assertTrue(errores.contains("horaApertura"));
        assertTrue(errores.contains("horaCierre"));
        assertFalse(errores.contains("rangoValido"));
    }
}
