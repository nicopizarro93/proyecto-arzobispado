package cl.duoc.santiago_sacro_api.visita;

import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.RecintoRepository;
import cl.duoc.santiago_sacro_api.ruta.RutaRepository;
import cl.duoc.santiago_sacro_api.usuario.Rol;
import cl.duoc.santiago_sacro_api.usuario.Usuario;
import cl.duoc.santiago_sacro_api.usuario.UsuarioRepository;
import cl.duoc.santiago_sacro_api.visita.dto.CambioVisitaRequest;
import cl.duoc.santiago_sacro_api.visita.dto.VisitaDto;
import cl.duoc.santiago_sacro_api.visita.dto.VisitaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitaServiceTest {

    private static final Long ID_USUARIO = 20L;
    private static final LocalDateTime EN_UNA_SEMANA = LocalDateTime.now().plusDays(7);

    @Mock
    private VisitaRepository visitaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private RutaRepository rutaRepository;

    private VisitaService visitaService;
    private Recinto museo;

    @BeforeEach
    void preparar() {
        visitaService = new VisitaService(visitaRepository, usuarioRepository, recintoRepository, rutaRepository);
        museo = new Recinto();
        museo.setId(3L);
        museo.setNombreOficial("Museo de Arte Sacro Los Aromos");
    }

    private Usuario cuenta(Rol rol) {
        Usuario u = new Usuario();
        u.setId(ID_USUARIO);
        u.setRol(rol);
        return u;
    }

    private Visita visita(EstadoVisita estado, LocalDateTime inicio) {
        Visita v = new Visita();
        v.setId(1L);
        v.setUsuario(cuenta(Rol.VISITANTE));
        v.setRecinto(museo);
        v.setInicio(inicio);
        v.setCantidadPersonas(4);
        v.setEstado(estado);
        return v;
    }

    private VisitaRequest alMuseo(String nivelCurso) {
        return new VisitaRequest(3L, null, EN_UNA_SEMANA, 4, nivelCurso, null);
    }

    @Test
    void agendarAUnRecintoLaDejaAgendada() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.VISITANTE)));
        when(recintoRepository.buscarActivoPorId(3L)).thenReturn(Optional.of(museo));
        when(visitaRepository.save(any(Visita.class))).thenAnswer(invocacion -> {
            Visita v = invocacion.getArgument(0);
            v.setId(50L);
            return v;
        });

        VisitaDto agendada = visitaService.agendar(ID_USUARIO, alMuseo(null));

        assertEquals(EstadoVisita.AGENDADA, agendada.estado());
        assertEquals("Museo de Arte Sacro Los Aromos", agendada.destino());
        assertNull(agendada.idRuta());
    }

    @Test
    void unDocenteSinCursoRecibe400() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.DOCENTE)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> visitaService.agendar(ID_USUARIO, alMuseo(null)));
        assertEquals(400, error.getStatusCode().value());
        verify(visitaRepository, never()).save(any());
    }

    @Test
    void unVisitanteConCursoRecibe400() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.VISITANTE)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> visitaService.agendar(ID_USUARIO, alMuseo("3° medio")));
        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void unaRutaAjenaODesactivadaResponde404() {
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(cuenta(Rol.DOCENTE)));
        when(rutaRepository.buscarSugeridaActiva(9L)).thenReturn(Optional.empty());
        when(rutaRepository.buscarPersonalActiva(9L, ID_USUARIO)).thenReturn(Optional.empty());
        VisitaRequest datos = new VisitaRequest(null, 9L, EN_UNA_SEMANA, 30, "3° medio", null);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> visitaService.agendar(ID_USUARIO, datos));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void noSePuedeCambiarUnaVisitaRealizada() {
        when(visitaRepository.buscarUnaDelUsuario(1L, ID_USUARIO))
                .thenReturn(Optional.of(visita(EstadoVisita.REALIZADA, LocalDateTime.now().minusDays(10))));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> visitaService.cambiar(ID_USUARIO, 1L, new CambioVisitaRequest(EN_UNA_SEMANA, 6)));
        assertEquals(409, error.getStatusCode().value());
    }

    @Test
    void unaVisitaAgendadaCuyaFechaYaPasoTampocoSeCambia() {
        when(visitaRepository.buscarUnaDelUsuario(1L, ID_USUARIO))
                .thenReturn(Optional.of(visita(EstadoVisita.AGENDADA, LocalDateTime.now().minusHours(2))));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> visitaService.cambiar(ID_USUARIO, 1L, new CambioVisitaRequest(EN_UNA_SEMANA, 6)));
        assertEquals(409, error.getStatusCode().value());
    }

    @Test
    void cancelarCancelaTambienLasSolicitudesDeGuia() {
        Visita visita = visita(EstadoVisita.AGENDADA, EN_UNA_SEMANA);
        when(visitaRepository.buscarUnaDelUsuario(1L, ID_USUARIO)).thenReturn(Optional.of(visita));

        VisitaDto cancelada = visitaService.cancelar(ID_USUARIO, 1L);

        assertEquals(EstadoVisita.CANCELADA, cancelada.estado());
        verify(visitaRepository).cancelarSolicitudesDeGuia(1L);
    }

    @Test
    void unaVisitaAjenaResponde404() {
        when(visitaRepository.buscarUnaDelUsuario(1L, ID_USUARIO)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> visitaService.cancelar(ID_USUARIO, 1L));
        assertEquals(404, error.getStatusCode().value());
        verify(visitaRepository, never()).cancelarSolicitudesDeGuia(any());
    }
}
