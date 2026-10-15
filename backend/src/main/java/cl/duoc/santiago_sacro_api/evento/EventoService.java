package cl.duoc.santiago_sacro_api.evento;

import cl.duoc.santiago_sacro_api.evento.dto.EventoDetalleDto;
import cl.duoc.santiago_sacro_api.evento.dto.EventoResumenDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoService {

    // Un evento sin término se considera vigente 24 horas desde su inicio (igual que la vista v_evento_mapa).
    private static final long HORAS_SIN_FIN = 24;

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoResumenDto> listarVigentes(TipoEvento tipo, Long idRecinto) {
        LocalDateTime ahora = LocalDateTime.now();
        return eventoRepository.buscarVigentes(ahora, ahora.minusHours(HORAS_SIN_FIN), tipo, idRecinto).stream()
                // Sin filtro de tipo, las aperturas y los cierres no van en la lista: se ven en la ficha.
                .filter(e -> tipo != null || !e.getTipo().esExcepcionDeHorario())
                .map(EventoResumenDto::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoDetalleDto obtener(Long id) {
        return eventoRepository.buscarPublicadoPorId(id)
                .map(EventoDetalleDto::desde)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un evento publicado con id " + id));
    }
}
