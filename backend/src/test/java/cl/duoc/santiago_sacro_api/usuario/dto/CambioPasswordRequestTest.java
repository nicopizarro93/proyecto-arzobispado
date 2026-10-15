package cl.duoc.santiago_sacro_api.usuario.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CambioPasswordRequestTest {

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

    private Set<String> camposConError(String actual, String nueva, String confirmar) {
        return validador.validate(new CambioPasswordRequest(actual, nueva, confirmar)).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void unCambioCorrectoNoTieneErrores() {
        assertTrue(camposConError("Prueba123", "Nueva1234", "Nueva1234").isEmpty());
    }

    @Test
    void laNuevaSinNumeroEsInvalida() {
        assertTrue(camposConError("Prueba123", "soloLetras", "soloLetras").contains("passwordNueva"));
    }

    @Test
    void siNoCoincidenEsInvalido() {
        assertTrue(camposConError("Prueba123", "Nueva1234", "Nueva9999").contains("passwordsCoinciden"));
    }

    @Test
    void laNuevaNoPuedeSerIgualALaActual() {
        assertTrue(camposConError("Prueba123", "Prueba123", "Prueba123").contains("distintaDeLaActual"));
    }
}