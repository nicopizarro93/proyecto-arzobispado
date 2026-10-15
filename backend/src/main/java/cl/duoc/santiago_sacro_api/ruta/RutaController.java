package cl.duoc.santiago_sacro_api.ruta;

import cl.duoc.santiago_sacro_api.ruta.dto.EstimacionDto;
import cl.duoc.santiago_sacro_api.ruta.dto.EstimacionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    // POST /api/rutas/estimar → tiempo estimado de una ruta, sin guardarla (HU-19)
    @PostMapping("/estimar")
    public EstimacionDto estimar(@Valid @RequestBody EstimacionRequest solicitud) {
        return rutaService.estimar(solicitud);
    }
}
