package cl.duoc.santiago_sacro_api.evento;

import cl.duoc.santiago_sacro_api.evento.dto.EventoDetalleDto;
import cl.duoc.santiago_sacro_api.evento.dto.EventoResumenDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.santiago_sacro_api.evento.dto.EventoGestionDto;
import cl.duoc.santiago_sacro_api.evento.dto.EventoRequest;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.usuario.Usuario;

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

    // Eventos vigentes de un recinto, incluidas aperturas y cierres (los usa la ficha).
    @Transactional(readOnly = true)
    public List<Evento> vigentesDelRecinto(Long idRecinto) {
        LocalDateTime ahora = LocalDateTime.now();
        return eventoRepository.buscarVigentes(ahora, ahora.minusHours(HORAS_SIN_FIN), null, idRecinto);
    }

    // ---- Gestión del encargado (HU-28 a HU-30) ----

    @Transactional(readOnly = true)
    public List<EventoGestionDto> historialDelRecinto(Long idRecinto) {
        return eventoRepository.findByRecintoIdOrderByInicioDesc(idRecinto).stream()
                .map(EventoGestionDto::desde)
                .toList();
    }

    @Transactional
    public EventoGestionDto publicar(Recinto recinto, Usuario encargado, EventoRequest datos) {
        Evento evento = new Evento();
        evento.setRecinto(recinto);
        evento.setPublicadoPor(encargado);
        copiar(datos, evento);
        return EventoGestionDto.desde(eventoRepository.save(evento));
    }

    @Transactional
    public EventoGestionDto editar(Recinto recinto, Long idEvento, EventoRequest datos) {
        Evento evento = buscarDelRecinto(recinto.getId(), idEvento);
        if (evento.getEstado() == EstadoEvento.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un evento cancelado no se puede editar");
        }
        copiar(datos, evento);
        return EventoGestionDto.desde(evento);
    }

    // Cancelar no borra: el evento deja de verse en la app, pero queda en el historial (HU-29).
    @Transactional
    public EventoGestionDto cancelar(Recinto recinto, Long idEvento) {
        Evento evento = buscarDelRecinto(recinto.getId(), idEvento);
        evento.setEstado(EstadoEvento.CANCELADO);
        return EventoGestionDto.desde(evento);
    }

    // Un evento de otro recinto responde 404, igual que uno que no existe.
    private Evento buscarDelRecinto(Long idRecinto, Long idEvento) {
        return eventoRepository.findByIdAndRecintoId(idEvento, idRecinto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el evento " + idEvento + " en tu recinto"));
    }

    private void copiar(EventoRequest datos, Evento evento) {
        evento.setTipo(datos.tipo());
        evento.setTitulo(datos.titulo());
        evento.setDescripcion(datos.descripcion());
        evento.setInicio(datos.inicio());
        evento.setFin(datos.fin());
    }
        
    @Transactional(readOnly = true)
    public EventoDetalleDto obtener(Long id) {
        return eventoRepository.buscarPublicadoPorId(id)
                .map(EventoDetalleDto::desde)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un evento publicado con id " + id));
    }
}
