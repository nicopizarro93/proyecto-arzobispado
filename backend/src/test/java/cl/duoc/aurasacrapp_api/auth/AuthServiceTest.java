package cl.duoc.aurasacrapp_api.auth;

import cl.duoc.aurasacrapp_api.auth.dto.RegistroRequest;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import cl.duoc.aurasacrapp_api.usuario.dto.UsuarioDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenService tokenService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void crearServicio() {
                   authService = new AuthService(usuarioRepository, passwordEncoder, tokenService);
    }

    private RegistroRequest solicitud(Rol rol, String institucion) {
        return new RegistroRequest("Marta", "Díaz", "Marta.Diaz@Ejemplo.cl",
                "clave1234", "clave1234", rol, institucion, true);
    }

    // Simula que la base guarda el usuario y le asigna el id 1.
    private void simularGuardado() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario u = invocacion.getArgument(0);
            u.setId(1L);
            return u;
        });
    }

    @Test
    void registraVisitanteConPasswordCifrada() {
        when(usuarioRepository.existsByCorreo("marta.diaz@ejemplo.cl")).thenReturn(false);
        simularGuardado();

        UsuarioDto respuesta = authService.registrar(solicitud(Rol.VISITANTE, "Colegio X"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertEquals("marta.diaz@ejemplo.cl", guardado.getCorreo());
        assertNotEquals("clave1234", guardado.getPasswordHash());
        assertTrue(passwordEncoder.matches("clave1234", guardado.getPasswordHash()));
        assertNull(guardado.getInstitucion(), "Un visitante no guarda institución");
        assertNotNull(guardado.getFechaAceptaPrivacidad());
        assertEquals(1L, respuesta.id().longValue());
    }

    @Test
    void docenteGuardaSuInstitucion() {
        when(usuarioRepository.existsByCorreo("marta.diaz@ejemplo.cl")).thenReturn(false);
        simularGuardado();

        UsuarioDto respuesta = authService.registrar(solicitud(Rol.DOCENTE, "Colegio San Esteban"));

        assertEquals(Rol.DOCENTE, respuesta.rol());
        assertEquals("Colegio San Esteban", respuesta.institucion());
    }

    @Test
    void correoRepetidoLanzaExcepcionYNoGuarda() {
        when(usuarioRepository.existsByCorreo("marta.diaz@ejemplo.cl")).thenReturn(true);

        assertThrows(CorreoYaRegistradoException.class,
                () -> authService.registrar(solicitud(Rol.VISITANTE, null)));
        verify(usuarioRepository, never()).save(any());
    }
}
