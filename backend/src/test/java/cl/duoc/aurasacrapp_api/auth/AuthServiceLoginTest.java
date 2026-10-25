package cl.duoc.aurasacrapp_api.auth;

import cl.duoc.aurasacrapp_api.auth.dto.LoginRequest;
import cl.duoc.aurasacrapp_api.auth.dto.LoginResponse;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

    private static final String CORREO = "marta.diaz@ejemplo.cl";

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

    private Usuario marta() {
        Usuario u = new Usuario();
        u.setId(7L);
        u.setNombre("Marta");
        u.setApellido("Díaz");
        u.setCorreo(CORREO);
        u.setRol(Rol.DOCENTE);
        u.setPasswordHash(passwordEncoder.encode("clave1234"));
        return u;
    }

    @Test
    void loginCorrectoEntregaTokenYReiniciaIntentos() {
        Usuario u = marta();
        u.setIntentosFallidos(3);
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(u));
        when(tokenService.generar(u)).thenReturn(new TokenGenerado("token-de-prueba", Instant.parse("2026-10-20T12:00:00Z")));

        LoginResponse respuesta = authService.login(new LoginRequest("Marta.Diaz@Ejemplo.cl", "clave1234"));

        assertEquals("token-de-prueba", respuesta.token());
        assertEquals("Bearer", respuesta.tipo());
        assertEquals(Rol.DOCENTE, respuesta.usuario().rol());
        assertEquals(0, u.getIntentosFallidos());
    }

    @Test
    void claveIncorrectaSumaUnIntento() {
        Usuario u = marta();
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(u));

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new LoginRequest(CORREO, "otraClave1")));
        assertEquals(1, u.getIntentosFallidos());
        verifyNoInteractions(tokenService);
    }

    @Test
    void quintoIntentoFallidoBloqueaLaCuenta() {
        Usuario u = marta();
        u.setIntentosFallidos(4);
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(u));

        assertThrows(CuentaBloqueadaException.class,
                () -> authService.login(new LoginRequest(CORREO, "otraClave1")));
        assertNotNull(u.getBloqueadoHasta());
        assertTrue(u.getBloqueadoHasta().isAfter(LocalDateTime.now()));
    }

    @Test
    void cuentaBloqueadaNoEntraAunqueLaClaveSeaCorrecta() {
        Usuario u = marta();
        u.setBloqueadoHasta(LocalDateTime.now().plusSeconds(30));
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(u));

        assertThrows(CuentaBloqueadaException.class,
                () -> authService.login(new LoginRequest(CORREO, "clave1234")));
        verifyNoInteractions(tokenService);
    }

    @Test
    void correoInexistenteDaElMismoErrorQueClaveIncorrecta() {
        when(usuarioRepository.findByCorreo("nadie@ejemplo.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new LoginRequest("nadie@ejemplo.cl", "clave1234")));
    }

    @Test
    void cuentaDeshabilitadaNoPuedeEntrar() {
        Usuario u = marta();
        u.setActivo(false);
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(u));

        assertThrows(CuentaDeshabilitadaException.class,
                () -> authService.login(new LoginRequest(CORREO, "clave1234")));
    }
}
