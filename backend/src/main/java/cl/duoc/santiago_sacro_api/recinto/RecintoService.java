package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.horario.HorarioRegularRepository;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioDto;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoResumenDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.santiago_sacro_api.evento.Evento;
import cl.duoc.santiago_sacro_api.evento.EventoService;
import cl.duoc.santiago_sacro_api.evento.dto.EventoResumenDto;
import cl.duoc.santiago_sacro_api.evento.dto.ExcepcionHorarioDto;
import java.util.List;

@Service
public class RecintoService {

    private final RecintoRepository recintoRepository;
    private final HorarioRegularRepository horarioRepository;
    private final EventoService eventoService;

    public RecintoService(RecintoRepository recintoRepository, HorarioRegularRepository horarioRepository,
                          EventoService eventoService) {
        this.recintoRepository = recintoRepository;
        this.horarioRepository = horarioRepository;
        this.eventoService = eventoService;
    }

    @Transactional(readOnly = true)
    public List<RecintoResumenDto> listar(String texto, Long idComuna, TipoRecinto tipo, EstadoApertura estado) {
        String filtro = (texto == null || texto.isBlank()) ? null : texto.trim();
        return recintoRepository.buscar(filtro, idComuna, tipo, estado).stream()
                .map(RecintoResumenDto::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecintoDetalleDto obtener(Long id) {
        Recinto recinto = recintoRepository.buscarActivoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un recinto activo con id " + id));
        List<HorarioDto> horarios = horarioRepository.findByRecintoIdOrderByDiaSemanaAscHoraAperturaAsc(id).stream()
                .map(HorarioDto::desde)
                .toList();
        // Una sola consulta de eventos vigentes: se separan las aperturas y cierres del resto.
        List<Evento> vigentes = eventoService.vigentesDelRecinto(id);
        List<ExcepcionHorarioDto> excepciones = vigentes.stream()
                .filter(e -> e.getTipo().esExcepcionDeHorario())
                .map(ExcepcionHorarioDto::desde)
                .toList();
        List<EventoResumenDto> proximosEventos = vigentes.stream()
                .filter(e -> !e.getTipo().esExcepcionDeHorario())
                .map(EventoResumenDto::desde)
                .toList();
        return RecintoDetalleDto.desde(recinto, horarios, excepciones, proximosEventos);
    }
}