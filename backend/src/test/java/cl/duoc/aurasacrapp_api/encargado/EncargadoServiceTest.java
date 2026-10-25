package cl.duoc.aurasacrapp_api.encargado;

import cl.duoc.aurasacrapp_api.auth.CuentaDeshabilitadaException;
import cl.duoc.aurasacrapp_api.horario.HorarioService;
import cl.duoc.aurasacrapp_api.horario.dto.HorarioRequest;
import cl.duoc.aurasacrapp_api.recinto.EstadoApertura;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.RecintoService;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import cl.duoc.aurasacrapp_api.evento.EventoService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import cl.duoc.aurasacrapp_api.evento.TipoEvento;
import cl.duoc.aurasacrapp_api.evento.dto.EventoRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EncargadoServiceTest {

    private static final Long ID_USUARIO = 10L;
    private static final LocalDateTime FECHA_ANTERIOR = LocalDateTime.of(2026, 1, 1, 9, 0);

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecintoService recintoService;

    @Mock
    private HorarioService horarioService;

    @Mock
    private EventoService eventoService;

    private EncargadoService encargadoService;
    private Recinto capilla;

    @BeforeEach
    void preparar() {
        encargadoService = new EncargadoService(usuarioRepository, recintoService, horarioService, eventoService);
        capilla = new Recinto();
        capilla.setId(1L);
        capilla.setEstadoApertura(EstadoApertura.CERRADO);
        capilla.setFechaEstado(FECHA_ANTERIOR);
    }

    private Usuario cuenta(Rol rol, boolean activa) {
        Usuario u = new Usuario();
        u.setId(ID_USUARIO);
        u.setRol(rol);
        u.setActivo(activa);
        u.setRecinto(rol == Rol.ENCARGADO ? capilla : null);
        return u;
    }

    @Test
    void cambiarEstadoGuardaElEstadoYLaFecha() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.ENCARGADO, true)));

        encargadoService.cambiarEstado(ID_USUARIO, EstadoApertura.ABIERTO);

        assertEquals(EstadoApertura.ABIERTO, capilla.getEstadoApertura());
        assertTrue(capilla.getFechaEstado().isAfter(FECHA_ANTERIOR));
        verify(recintoService).obtener(1L);
    }

    @Test
    void cuentaQueNoEsEncargadoRecibeAccesoDenegado() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.VISITANTE, true)));

        assertThrows(AccessDeniedException.class,
                () -> encargadoService.cambiarEstado(ID_USUARIO, EstadoApertura.ABIERTO));
        verifyNoInteractions(recintoService);
    }

    @Test
    void cuentaDeshabilitadaNoPuedeCambiarElEstado() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.ENCARGADO, false)));

        assertThrows(CuentaDeshabilitadaException.class,
                () -> encargadoService.cambiarEstado(ID_USUARIO, EstadoApertura.ABIERTO));
        assertEquals(EstadoApertura.CERRADO, capilla.getEstadoApertura());
    }

    @Test
    void losHorariosSeGuardanEnElRecintoDeLaCuenta() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.ENCARGADO, true)));
        HorarioRequest datos = new HorarioRequest(1, LocalTime.of(19, 30), LocalTime.of(21, 0));

        encargadoService.agregarHorario(ID_USUARIO, datos);

        verify(horarioService).crear(capilla, datos);
    }

    @Test
    void publicarEventoUsaElRecintoYLaCuentaDelToken() {
        Usuario encargado = cuenta(Rol.ENCARGADO, true);
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(encargado));
        when(usuarioRepository.getReferenceById(ID_USUARIO)).thenReturn(encargado);
        EventoRequest datos = new EventoRequest(TipoEvento.AVISO, "Aviso", null,
                LocalDateTime.of(2026, 11, 5, 9, 0), null);

        encargadoService.publicarEvento(ID_USUARIO, datos);

        verify(eventoService).publicar(capilla, encargado, datos);
    }    
}
