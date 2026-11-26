package cl.duoc.aurasacrapp_api.admin;

import cl.duoc.aurasacrapp_api.admin.dto.EncargadoDto;
import cl.duoc.aurasacrapp_api.admin.dto.EncargadoRequest;
import cl.duoc.aurasacrapp_api.auth.CorreoYaRegistradoException;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.RecintoRepository;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EncargadoAdminServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RecintoRepository recintoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private EncargadoAdminService servicio;
    private Recinto capilla;
    private EncargadoRequest solicitud;

    @BeforeEach
    void preparar() {
        servicio = new EncargadoAdminService(usuarioRepository, recintoRepository, passwordEncoder);
        capilla = new Recinto();
        capilla.setId(1L);
        capilla.setNombreOficial("Capilla del Buen Pastor");
        solicitud = new EncargadoRequest("  Ana ", "Rojas", " ANA@Correo.CL ", "Clave1234", 1L);
    }

    @Test
    void creaLaCuentaConRolEncargadoYLaClaveCifrada() {
        when(usuarioRepository.existsByCorreo("ana@correo.cl")).thenReturn(false);
        when(recintoRepository.buscarActivoPorId(1L)).thenReturn(Optional.of(capilla));
        when(usuarioRepository.existsByRolAndRecintoIdAndActivoTrue(Rol.ENCARGADO, 1L)).thenReturn(false);
        when(passwordEncoder.encode("Clave1234")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        EncargadoDto resultado = servicio.crear(solicitud);

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(guardado.capture());
        assertEquals(Rol.ENCARGADO, guardado.getValue().getRol());
        assertEquals("hash-bcrypt", guardado.getValue().getPasswordHash());
        assertEquals("ana@correo.cl", guardado.getValue().getCorreo());
        assertEquals("Ana", guardado.getValue().getNombre());
        assertEquals("Capilla del Buen Pastor", resultado.recinto());
        assertTrue(resultado.activo());
    }

    @Test
    void noCreaUnaCuentaConUnCorreoYaRegistrado() {
        when(usuarioRepository.existsByCorreo("ana@correo.cl")).thenReturn(true);

        assertThrows(CorreoYaRegistradoException.class, () -> servicio.crear(solicitud));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void noCreaUnaCuentaParaUnRecintoQueNoExisteOEstaDeBaja() {
        when(usuarioRepository.existsByCorreo("ana@correo.cl")).thenReturn(false);
        when(recintoRepository.buscarActivoPorId(1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> servicio.crear(solicitud));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void unRecintoNoTieneDosEncargadosActivos() {
        when(usuarioRepository.existsByCorreo("ana@correo.cl")).thenReturn(false);
        when(recintoRepository.buscarActivoPorId(1L)).thenReturn(Optional.of(capilla));
        when(usuarioRepository.existsByRolAndRecintoIdAndActivoTrue(Rol.ENCARGADO, 1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> servicio.crear(solicitud));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void desactivarUnaCuentaLaDejaInactiva() {
        Usuario encargado = new Usuario();
        encargado.setId(7L);
        encargado.setRol(Rol.ENCARGADO);
        encargado.setRecinto(capilla);
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(encargado));

        EncargadoDto resultado = servicio.cambiarActivo(7L, false);

        assertFalse(resultado.activo());
        assertFalse(encargado.getActivo());
    }

    @Test
    void noSeReactivaUnaCuentaSiElRecintoYaTieneOtroEncargado() {
        Usuario encargado = new Usuario();
        encargado.setId(7L);
        encargado.setRol(Rol.ENCARGADO);
        encargado.setActivo(false);
        encargado.setRecinto(capilla);
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(encargado));
        when(usuarioRepository.existsByRolAndRecintoIdAndActivoTrue(Rol.ENCARGADO, 1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> servicio.cambiarActivo(7L, true));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertFalse(encargado.getActivo());
    }

    @Test
    void soloSePuedenGestionarCuentasDeEncargado() {
        Usuario visitante = new Usuario();
        visitante.setId(4L);
        visitante.setRol(Rol.VISITANTE);
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(visitante));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> servicio.cambiarActivo(4L, false));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }
}
