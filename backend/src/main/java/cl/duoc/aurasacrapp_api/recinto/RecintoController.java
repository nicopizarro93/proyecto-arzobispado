package cl.duoc.aurasacrapp_api.recinto;

import cl.duoc.aurasacrapp_api.recinto.dto.RecintoDetalleDto;
import cl.duoc.aurasacrapp_api.recinto.dto.RecintoResumenDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recintos")
public class RecintoController {

    private final RecintoService recintoService;

    public RecintoController(RecintoService recintoService) {
        this.recintoService = recintoService;
    }

    // GET /api/recintos?q=capilla&idComuna=19&tipo=TEMPLO&estado=ABIERTO (todos opcionales)
    @GetMapping
    public List<RecintoResumenDto> listar(
            @RequestParam(name = "q", required = false) String texto,
            @RequestParam(name = "idComuna", required = false) Long idComuna,
            @RequestParam(name = "tipo", required = false) TipoRecinto tipo,
            @RequestParam(name = "estado", required = false) EstadoApertura estado) {
        return recintoService.listar(texto, idComuna, tipo, estado);
    }

    // GET /api/recintos/1
    @GetMapping("/{id}")
    public RecintoDetalleDto obtener(@PathVariable("id") Long id) {
        return recintoService.obtener(id);
    }
}
