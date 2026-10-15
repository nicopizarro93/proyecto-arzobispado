package cl.duoc.santiago_sacro_api.visita;

import cl.duoc.santiago_sacro_api.visita.dto.VisitaDto;
import cl.duoc.santiago_sacro_api.visita.dto.VisitaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/** Visitas de visitantes y docentes (HU-21, HU-22, HU-25). */
@RestController
@RequestMapping("/api/visitas")
public class VisitaController {

    private final VisitaService visitaService;

    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    // POST /api/visitas → 201 con la visita agendada
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitaDto agendar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody VisitaRequest solicitud) {
        return visitaService.agendar(idUsuario(jwt), solicitud);
    }

    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}