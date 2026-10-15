package cl.duoc.santiago_sacro_api.usuario;

import cl.duoc.santiago_sacro_api.usuario.dto.CambioPasswordRequest;
import cl.duoc.santiago_sacro_api.usuario.dto.PerfilRequest;
import cl.duoc.santiago_sacro_api.usuario.dto.UsuarioDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final Long ID = 30L;

    @Mock
    private UsuarioRepository usuarioRepository;

    // BCrypt real, igual que en las pruebas del login.
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UsuarioService usuarioService;

    @BeforeEach
    void preparar() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    // Crea la cuenta y deja listo que el repositorio la encuentre.
    private Usuario cuenta(Rol rol) {
        Usuario u = new Usuario();
        u.setId(ID);
        u.setRol(rol);
        u.setNombre("Lucía");
        u.setApellido("Herrera");
        u.setCorreo("visitante@santiagosacro.cl");
        u.setPasswordHash(passwordEncoder.encode("Prueba123"));
        if (rol == Rol.DOCENTE) {
            u.setInstitucion("Colegio San Esteban");
        }
        when(usuarioRepository.findById(ID)).thenReturn(Optional.of(u));
        return u;
    }

    @Test
    void actualizarElPerfilNoCambiaElCorreo() {
        Usuario lucia = cuenta(Rol.VISITANTE);

        UsuarioDto actualizado = usuarioService.actualizarPerfil(ID,
                new PerfilRequest("Lucía", "Herrera Soto", "Colegio X"));

        assertEquals("Herrera Soto", actualizado.apellido());
        assertEquals("visitante@santiagosacro.cl", actualizado.correo());
        assertNull(lucia.getInstitucion()); // un visitante no tiene institución
    }

    @Test
    void unDocenteSinInstitucionRecibe400() {
        Usuario jorge = cuenta(Rol.DOCENTE);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> usuarioService.actualizarPerfil(ID, new PerfilRequest("Jorge", "Contreras", null)));
        assertEquals(400, error.getStatusCode().value());
        assertEquals("Colegio San Esteban", jorge.getInstitucion());
    }

    @Test
    void unDocenteActualizaSuInstitucion() {
        Usuario jorge = cuenta(Rol.DOCENTE);

        usuarioService.actualizarPerfil(ID, new PerfilRequest("Jorge", "Contreras", "Liceo Poniente"));

        assertEquals("Liceo Poniente", jorge.getInstitucion());
    }

    @Test
    void conLaPasswordActualIncorrectaNoSeCambia() {
        Usuario lucia = cuenta(Rol.VISITANTE);
        String hashAnterior = lucia.getPasswordHash();

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> usuarioService.cambiarPassword(ID,
                        new CambioPasswordRequest("Otra1234", "Nueva1234", "Nueva1234")));
        assertEquals(400, error.getStatusCode().value());
        assertEquals(hashAnterior, lucia.getPasswordHash());
    }

    @Test
    void laPasswordNuevaSeGuardaCifrada() {
        Usuario lucia = cuenta(Rol.VISITANTE);

        usuarioService.cambiarPassword(ID, new CambioPasswordRequest("Prueba123", "Nueva1234", "Nueva1234"));

        assertNotEquals("Nueva1234", lucia.getPasswordHash());
        assertTrue(passwordEncoder.matches("Nueva1234", lucia.getPasswordHash()));
    }
}