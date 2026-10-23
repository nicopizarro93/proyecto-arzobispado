package cl.duoc.santiago_sacro_api.comuna;

import cl.duoc.santiago_sacro_api.comuna.dto.ComunaDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    private final ComunaService comunaService;

    public ComunaController(ComunaService comunaService) {
        this.comunaService = comunaService;
    }

    // GET /api/comunas → catálogo para el filtro "Comuna" de la búsqueda (HU-09)
    @GetMapping
    public List<ComunaDto> listar() {
        return comunaService.listar();
    }
}
