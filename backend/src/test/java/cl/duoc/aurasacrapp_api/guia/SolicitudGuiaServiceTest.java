package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.guia.dto.SolicitudUsuarioDto;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.RecintoRepository;
import cl.duoc.aurasacrapp_api.visita.EstadoVisita;
import cl.duoc.aurasacrapp_api.visita.Visita;
import cl.duoc.aurasacrapp_api.visita.VisitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudGuiaServiceTest {

    private static final Long ID_USUARIO = 10L;
    private static final Long ID_VISITA = 7L;

    @Mock
    private SolicitudGuiaRepository solicitudRepository;
    @Mock
    private VisitaRepository visitaRepository;
    @Mock
    private RecintoRepository recintoRepository;

    private SolicitudGuiaService servicio;
    private Recinto capilla;
    private Visita visita;

    @BeforeEach
    void preparar() {
        servicio = new SolicitudGuiaService(solicitudRepository, visitaRepository, recintoRepository);

        capilla = new Recinto();
        capilla.setId(1L);
        capilla.setNombreOficial("Capilla del Buen Pastor");

        visita = new Visita();
        visita.setId(ID_VISITA);
        visita.setRecinto(capilla);
        visita.setInicio(LocalDateTime.now().plusDays(10));
        visita.setEstado(EstadoVisita.AGENDADA);
    }

    private void visitaDelUsuario() {
        when(visitaRepository.buscarUnaDelUsuario(ID_VISITA, ID_USUARIO)).thenReturn(Optional.of(visita));
    }

    @Test
    void solicitarCreaUnaSolicitudPendienteParaElRecintoDeLaVisita() {
        visitaDelUsuario();
        when(recintoRepository.buscarActivoPorId(1L)).thenReturn(Optional.of(capilla));
        when(solicitudRepository.existsByVisitaIdAndRecintoId(ID_VISITA, 1L)).thenReturn(false);
        when(solicitudRepository.save(any(SolicitudGuia.class))).thenAnswer(inv -> inv.getArgument(0));

        SolicitudUsuarioDto resultado = servicio.solicitar(ID_USUARIO, ID_VISITA, 1L);

        assertEquals(EstadoSolicitud.PENDIENTE, resultado.estado());
        assertEquals("Capilla del Buen Pastor", resultado.recinto());
        assertNull(resultado.guia());
    }

    @Test
    void noSePideDosVecesUnGuiaParaElMismoRecintoDeLaMismaVisita() {
        visitaDelUsuario();
        when(recintoRepository.buscarActivoPorId(1L)).thenReturn(Optional.of(capilla));
        when(solicitudRepository.existsByVisitaIdAndRecintoId(ID_VISITA, 1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.solicitar(ID_USUARIO, ID_VISITA, 1L));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void noSePideGuiaParaUnRecintoQueNoEsDeLaVisita() {
        visitaDelUsuario();

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.solicitar(ID_USUARIO, ID_VISITA, 99L));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(recintoRepository);
    }

    @Test
    void noSePideGuiaParaUnaVisitaCancelada() {
        visita.setEstado(EstadoVisita.CANCELADA);
        visitaDelUsuario();

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.solicitar(ID_USUARIO, ID_VISITA, 1L));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    @Test
    void unaVisitaAjenaResponde404() {
        when(visitaRepository.buscarUnaDelUsuario(ID_VISITA, ID_USUARIO)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.solicitar(ID_USUARIO, ID_VISITA, 1L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }
}
