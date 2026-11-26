package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.guia.dto.SolicitudGuiaRequest;
import cl.duoc.aurasacrapp_api.guia.dto.SolicitudUsuarioDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Solicitudes de guía de quien agenda la visita (HU-23 y HU-24). Cuelga de /api/visitas,
 * así que ya exige un token de visitante o docente.
 */
@RestController
@RequestMapping("/api/visitas")
public class SolicitudGuiaController {

    private final SolicitudGuiaService solicitudService;

    public SolicitudGuiaController(SolicitudGuiaService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // POST /api/visitas/4/solicitudes-guia  con {"idRecinto": 1} → 201 con la solicitud pendiente
    @PostMapping("/{idVisita}/solicitudes-guia")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudUsuarioDto solicitar(@AuthenticationPrincipal Jwt jwt, @PathVariable("idVisita") Long idVisita,
                                         @Valid @RequestBody SolicitudGuiaRequest solicitud) {
        return solicitudService.solicitar(idUsuario(jwt), idVisita, solicitud.idRecinto());
    }

    // GET /api/visitas/solicitudes-guia → todas mis solicitudes de guía
    @GetMapping("/solicitudes-guia")
    public List<SolicitudUsuarioDto> misSolicitudes(@AuthenticationPrincipal Jwt jwt) {
        return solicitudService.misSolicitudes(idUsuario(jwt));
    }

    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
