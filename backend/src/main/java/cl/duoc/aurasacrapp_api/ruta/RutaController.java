package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.ruta.dto.EstimacionDto;
import cl.duoc.aurasacrapp_api.ruta.dto.EstimacionRequest;
import cl.duoc.aurasacrapp_api.ruta.dto.RutaDetalleDto;
import cl.duoc.aurasacrapp_api.ruta.dto.RutaResumenDto;
import jakarta.validation.Valid;

import java.util.List;

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

    // GET /api/rutas → rutas sugeridas activas (HU-17)
    @GetMapping
    public List<RutaResumenDto> sugeridas() {
        return rutaService.sugeridas();
    }

    // GET /api/rutas/1 → detalle de una ruta sugerida con sus paradas y tramos
    @GetMapping("/{id}")
    public RutaDetalleDto sugerida(@PathVariable("id") Long id) {
        return rutaService.sugerida(id);
    }    
}
