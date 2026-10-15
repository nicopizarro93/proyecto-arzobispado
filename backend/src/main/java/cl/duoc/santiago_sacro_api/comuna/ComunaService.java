package cl.duoc.santiago_sacro_api.comuna;

import cl.duoc.santiago_sacro_api.comuna.dto.ComunaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComunaService {

    private final ComunaRepository comunaRepository;

    public ComunaService(ComunaRepository comunaRepository) {
        this.comunaRepository = comunaRepository;
    }

    @Transactional(readOnly = true)
    public List<ComunaDto> listar() {
        return comunaRepository.findAllByOrderByNombreAsc().stream()
                .map(ComunaDto::desde)
                .toList();
    }
}