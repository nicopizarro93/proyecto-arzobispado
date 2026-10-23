package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.comuna.Comuna;
import cl.duoc.santiago_sacro_api.evento.Evento;
import cl.duoc.santiago_sacro_api.evento.EventoService;
import cl.duoc.santiago_sacro_api.evento.TipoEvento;
import cl.duoc.santiago_sacro_api.horario.HorarioRegular;
import cl.duoc.santiago_sacro_api.horario.HorarioRegularRepository;
import cl.duoc.santiago_sacro_api.multimedia.RecursoMultimedia;
import cl.duoc.santiago_sacro_api.multimedia.RecursoMultimediaRepository;
import cl.duoc.santiago_sacro_api.multimedia.TipoRecurso;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecintoServiceTest {

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private HorarioRegularRepository horarioRepository;

    @Mock
    private EventoService eventoService;

    @Mock
    private RecursoMultimediaRepository multimediaRepository;

    private RecintoService recintoService;
    private Recinto capilla;

    @BeforeEach
    void preparar() {
        recintoService = new RecintoService(recintoRepository, horarioRepository, eventoService, multimediaRepository);
        Comuna maipu = new Comuna();
        maipu.setId(19L);
        maipu.setNombre("Maipú");
        capilla = new Recinto();
        capilla.setId(1L);
        capilla.setNombreOficial("Capilla del Buen Pastor");
        capilla.setCalle("Pasaje El Llano");
        capilla.setNumeroCalle("118");
        capilla.setComuna(maipu);
        capilla.setEstadoApertura(EstadoApertura.ABIERTO);
    }

    private RecursoMultimedia imagen(Long id, String titulo, int orden) {
        RecursoMultimedia r = new RecursoMultimedia();
        r.setId(id);
        r.setRecinto(capilla);
        r.setTipo(TipoRecurso.IMAGEN);
        r.setUrl("https://picsum.photos/seed/prueba-" + id + "/800/600");
        r.setTitulo(titulo);
        r.setOrden(orden);
        return r;
    }

    private HorarioRegular lunes() {
        HorarioRegular h = new HorarioRegular();
        h.setId(1L);
        h.setRecinto(capilla);
        h.setDiaSemana(1);
        h.setHoraApertura(LocalTime.of(9, 0));
        h.setHoraCierre(LocalTime.of(13, 0));
        return h;
    }

    private Evento evento(Long id, TipoEvento tipo, String titulo) {
        Evento e = new Evento();
        e.setId(id);
        e.setRecinto(capilla);
        e.setTipo(tipo);
        e.setTitulo(titulo);
        e.setInicio(LocalDateTime.of(2026, 11, 5, 19, 0));
        return e;
    }

    @Test
    void laFichaJuntaGaleriaHorariosEventosYExcepciones() {
        when(recintoRepository.buscarActivoPorId(1L)).thenReturn(Optional.of(capilla));
        when(multimediaRepository.findByRecintoIdOrderByOrdenAsc(1L))
                .thenReturn(List.of(imagen(1L, "Fachada principal", 1), imagen(2L, "Nave central", 2)));
        when(horarioRepository.findByRecintoIdOrderByDiaSemanaAscHoraAperturaAsc(1L)).thenReturn(List.of(lunes()));
        when(eventoService.vigentesDelRecinto(1L)).thenReturn(List.of(
                evento(10L, TipoEvento.CONCIERTO, "Concierto del coro"),
                evento(11L, TipoEvento.CIERRE_ESPECIAL, "Limpieza de vitrales")));

        RecintoDetalleDto ficha = recintoService.obtener(1L);

        assertEquals("Pasaje El Llano 118, Maipú", ficha.direccion());
        assertEquals(List.of("Fachada principal", "Nave central"),
                ficha.galeria().stream().map(g -> g.titulo()).toList());
        assertEquals("Lunes", ficha.horarios().get(0).dia());
        assertEquals(1, ficha.excepciones().size());
        assertEquals("Limpieza de vitrales", ficha.excepciones().get(0).motivo());
        assertEquals(1, ficha.proximosEventos().size());
        assertEquals(TipoEvento.CONCIERTO, ficha.proximosEventos().get(0).tipo());
    }

    @Test
    void unRecintoQueNoExisteResponde404SinConsultarLoDemas() {
        when(recintoRepository.buscarActivoPorId(99L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> recintoService.obtener(99L));
        assertEquals(404, error.getStatusCode().value());
        verifyNoInteractions(horarioRepository, eventoService, multimediaRepository);
    }

    @Test
    void laBusquedaLimpiaEspaciosEIgnoraElTextoVacio() {
        recintoService.listar("  capilla  ", null, null, null);
        recintoService.listar("   ", null, null, null);

        verify(recintoRepository).buscar("capilla", null, null, null);
        verify(recintoRepository).buscar(null, null, null, null);
    }
}