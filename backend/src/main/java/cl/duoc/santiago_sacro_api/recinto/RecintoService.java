package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.horario.HorarioRegularRepository;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioDto;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoResumenDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecintoService {

    private final RecintoRepository recintoRepository;
    private final HorarioRegularRepository horarioRepository;

    public RecintoService(RecintoRepository recintoRepository, HorarioRegularRepository horarioRepository) {
        this.recintoRepository = recintoRepository;
        this.horarioRepository = horarioRepository;
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
        return RecintoDetalleDto.desde(recinto, horarios);
    }
}