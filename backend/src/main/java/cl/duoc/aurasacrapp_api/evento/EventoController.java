package cl.duoc.aurasacrapp_api.evento;

import cl.duoc.aurasacrapp_api.evento.dto.EventoDetalleDto;
import cl.duoc.aurasacrapp_api.evento.dto.EventoResumenDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // GET /api/eventos?tipo=CONCIERTO&idRecinto=1 (los dos filtros son opcionales)
    @GetMapping
    public List<EventoResumenDto> listar(
            @RequestParam(name = "tipo", required = false) TipoEvento tipo,
            @RequestParam(name = "idRecinto", required = false) Long idRecinto) {
        return eventoService.listarVigentes(tipo, idRecinto);
    }

    // GET /api/eventos/1
    @GetMapping("/{id}")
    public EventoDetalleDto obtener(@PathVariable("id") Long id) {
        return eventoService.obtener(id);
    }
}
