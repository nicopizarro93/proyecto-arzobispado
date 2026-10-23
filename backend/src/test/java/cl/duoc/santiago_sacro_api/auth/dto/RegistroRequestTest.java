package cl.duoc.santiago_sacro_api.auth.dto;

import cl.duoc.santiago_sacro_api.usuario.Rol;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RegistroRequestTest {

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

    private RegistroRequest solicitud(String password, String confirmar, Rol rol, String institucion, Boolean acepta) {
        return new RegistroRequest("Marta", "Díaz", "marta.diaz@ejemplo.cl", password, confirmar, rol, institucion, acepta);
    }

    private Set<String> camposConError(RegistroRequest solicitud) {
        return validador.validate(solicitud).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void solicitudValidaNoTieneErrores() {
        assertTrue(camposConError(solicitud("clave1234", "clave1234", Rol.DOCENTE, "Colegio San Esteban", true)).isEmpty());
    }

    @Test
    void limpiaEspaciosYDejaElCorreoEnMinusculas() {
        RegistroRequest r = new RegistroRequest("  Marta ", " Díaz ", "  Marta.Diaz@Ejemplo.CL ",
                "clave1234", "clave1234", Rol.VISITANTE, null, true);
        assertEquals("Marta", r.nombre());
        assertEquals("marta.diaz@ejemplo.cl", r.correo());
    }

    @Test
    void passwordSinNumeroEsInvalida() {
        assertTrue(camposConError(solicitud("clavesegura", "clavesegura", Rol.VISITANTE, null, true)).contains("password"));
    }

    @Test
    void passwordsDistintasSonInvalidas() {
        assertTrue(camposConError(solicitud("clave1234", "clave9999", Rol.VISITANTE, null, true)).contains("passwordsCoinciden"));
    }

    @Test
    void docenteSinInstitucionEsInvalido() {
        assertTrue(camposConError(solicitud("clave1234", "clave1234", Rol.DOCENTE, null, true)).contains("institucionValida"));
    }

    @Test
    void noSePuedeRegistrarComoEncargado() {
        assertTrue(camposConError(solicitud("clave1234", "clave1234", Rol.ENCARGADO, null, true)).contains("rolPermitido"));
    }

    @Test
    void debeAceptarLaPrivacidad() {
        assertTrue(camposConError(solicitud("clave1234", "clave1234", Rol.VISITANTE, null, false)).contains("aceptaPrivacidad"));
    }
}
