package cl.duoc.aurasacrapp_api.evento;

import cl.duoc.aurasacrapp_api.evento.dto.EventoGestionDto;
import cl.duoc.aurasacrapp_api.evento.dto.EventoRequest;
import cl.duoc.aurasacrapp_api.evento.dto.EventoResumenDto;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 5, 19, 0);

    @Mock
    private EventoRepository eventoRepository;

    private EventoService eventoService;
    private Recinto capilla;

    @BeforeEach
    void preparar() {
        eventoService = new EventoService(eventoRepository);
        capilla = new Recinto();
        capilla.setId(1L);
        capilla.setNombreOficial("Capilla del Buen Pastor");
    }

    private Evento evento(Long id, TipoEvento tipo, EstadoEvento estado) {
        Evento e = new Evento();
        e.setId(id);
        e.setRecinto(capilla);
        e.setTipo(tipo);
        e.setTitulo("Evento " + id);
        e.setInicio(INICIO);
        e.setEstado(estado);
        return e;
    }

    private EventoRequest solicitud(TipoEvento tipo) {
        return new EventoRequest(tipo, "Concierto de prueba", null, INICIO, INICIO.plusHours(1));
    }

    @Test
    void laListaSinTipoNoIncluyeAperturasNiCierres() {
        when(eventoRepository.buscarVigentes(any(), any(), isNull(), isNull())).thenReturn(List.of(
                evento(1L, TipoEvento.CONCIERTO, EstadoEvento.PUBLICADO),
                evento(5L, TipoEvento.CIERRE_ESPECIAL, EstadoEvento.PUBLICADO)));

        List<EventoResumenDto> lista = eventoService.listarVigentes(null, null);

        assertEquals(1, lista.size());
        assertEquals(TipoEvento.CONCIERTO, lista.get(0).tipo());
    }

    @Test
    void laListaConTipoRespetaElFiltro() {
        when(eventoRepository.buscarVigentes(any(), any(), eq(TipoEvento.CIERRE_ESPECIAL), isNull())).thenReturn(List.of(
                evento(5L, TipoEvento.CIERRE_ESPECIAL, EstadoEvento.PUBLICADO)));

        List<EventoResumenDto> lista = eventoService.listarVigentes(TipoEvento.CIERRE_ESPECIAL, null);

        assertEquals(1, lista.size());
        assertEquals(TipoEvento.CIERRE_ESPECIAL, lista.get(0).tipo());
    }

    @Test
    void unEventoSinTerminoSeConsideraVigente24Horas() {
        eventoService.listarVigentes(null, null);

        ArgumentCaptor<LocalDateTime> ahora = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> limite = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(eventoRepository).buscarVigentes(ahora.capture(), limite.capture(), isNull(), isNull());
        assertEquals(ahora.getValue().minusHours(24), limite.getValue());
    }

    @Test
    void publicarGuardaElRecintoYQuienPublica() {
        Usuario encargado = new Usuario();
        encargado.setId(10L);
        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocacion -> {
            Evento e = invocacion.getArgument(0);
            e.setId(50L);
            return e;
        });

        EventoGestionDto publicado = eventoService.publicar(capilla, encargado, solicitud(TipoEvento.CONCIERTO));

        ArgumentCaptor<Evento> guardado = ArgumentCaptor.forClass(Evento.class);
        verify(eventoRepository).save(guardado.capture());
        assertSame(capilla, guardado.getValue().getRecinto());
        assertSame(encargado, guardado.getValue().getPublicadoPor());
        assertEquals(50L, publicado.id());
        assertEquals(EstadoEvento.PUBLICADO, publicado.estado());
    }

    @Test
    void editarUnEventoCanceladoRespondeConflicto() {
        Evento cancelado = evento(7L, TipoEvento.CONCIERTO, EstadoEvento.CANCELADO);
        when(eventoRepository.findByIdAndRecintoId(7L, 1L)).thenReturn(Optional.of(cancelado));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> eventoService.editar(capilla, 7L, solicitud(TipoEvento.CONCIERTO)));
        assertEquals(409, error.getStatusCode().value());
        assertEquals("Evento 7", cancelado.getTitulo());
    }

    @Test
    void cancelarCambiaElEstadoSinBorrar() {
        Evento publicado = evento(1L, TipoEvento.CONCIERTO, EstadoEvento.PUBLICADO);
        when(eventoRepository.findByIdAndRecintoId(1L, 1L)).thenReturn(Optional.of(publicado));

        EventoGestionDto resultado = eventoService.cancelar(capilla, 1L);

        assertEquals(EstadoEvento.CANCELADO, resultado.estado());
        verify(eventoRepository, never()).delete(any());
    }

    @Test
    void eventoDeOtroRecintoRespondeNoEncontrado() {
        when(eventoRepository.findByIdAndRecintoId(8L, 1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> eventoService.cancelar(capilla, 8L));
        assertEquals(404, error.getStatusCode().value());
    }
}
