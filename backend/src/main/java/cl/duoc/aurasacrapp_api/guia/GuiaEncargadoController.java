package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.guia.dto.AsignarGuiaRequest;
import cl.duoc.aurasacrapp_api.guia.dto.DisponibilidadRequest;
import cl.duoc.aurasacrapp_api.guia.dto.GuiaActivoRequest;
import cl.duoc.aurasacrapp_api.guia.dto.GuiaDto;
import cl.duoc.aurasacrapp_api.guia.dto.GuiaRequest;
import cl.duoc.aurasacrapp_api.guia.dto.RechazarSolicitudRequest;
import cl.duoc.aurasacrapp_api.guia.dto.SolicitudRecintoDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Guías y solicitudes de guía del recinto del encargado (HU-32 y HU-33). Solo rol ENCARGADO. */
@RestController
@RequestMapping("/api/encargado/recinto")
public class GuiaEncargadoController {

    private final GuiaEncargadoService guiaService;

    public GuiaEncargadoController(GuiaEncargadoService guiaService) {
        this.guiaService = guiaService;
    }

    // GET /api/encargado/recinto/guias → mis guías con su disponibilidad
    @GetMapping("/guias")
    public List<GuiaDto> guias(@AuthenticationPrincipal Jwt jwt) {
        return guiaService.guias(idUsuario(jwt));
    }

    // POST /api/encargado/recinto/guias → 201 con el guía registrado
    @PostMapping("/guias")
    @ResponseStatus(HttpStatus.CREATED)
    public GuiaDto crearGuia(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody GuiaRequest solicitud) {
        return guiaService.crearGuia(idUsuario(jwt), solicitud);
    }

    // PUT /api/encargado/recinto/guias/3 → cambia nombre y apellido
    @PutMapping("/guias/{idGuia}")
    public GuiaDto editarGuia(@AuthenticationPrincipal Jwt jwt, @PathVariable("idGuia") Long idGuia,
                              @Valid @RequestBody GuiaRequest solicitud) {
        return guiaService.editarGuia(idUsuario(jwt), idGuia, solicitud);
    }

    // PATCH /api/encargado/recinto/guias/3/activo  con {"activo": false}
    @PatchMapping("/guias/{idGuia}/activo")
    public GuiaDto cambiarActivo(@AuthenticationPrincipal Jwt jwt, @PathVariable("idGuia") Long idGuia,
                                 @Valid @RequestBody GuiaActivoRequest solicitud) {
        return guiaService.cambiarActivo(idUsuario(jwt), idGuia, solicitud.activo());
    }

    // POST /api/encargado/recinto/guias/3/disponibilidad → 201 con el guía y sus bloques
    @PostMapping("/guias/{idGuia}/disponibilidad")
    @ResponseStatus(HttpStatus.CREATED)
    public GuiaDto agregarDisponibilidad(@AuthenticationPrincipal Jwt jwt, @PathVariable("idGuia") Long idGuia,
                                         @Valid @RequestBody DisponibilidadRequest solicitud) {
        return guiaService.agregarDisponibilidad(idUsuario(jwt), idGuia, solicitud);
    }

    // DELETE /api/encargado/recinto/guias/3/disponibilidad/9 → el guía con sus bloques restantes
    @DeleteMapping("/guias/{idGuia}/disponibilidad/{idDisponibilidad}")
    public GuiaDto eliminarDisponibilidad(@AuthenticationPrincipal Jwt jwt, @PathVariable("idGuia") Long idGuia,
                                          @PathVariable("idDisponibilidad") Long idDisponibilidad) {
        return guiaService.eliminarDisponibilidad(idUsuario(jwt), idGuia, idDisponibilidad);
    }

    // GET /api/encargado/recinto/solicitudes-guia → solicitudes de mi recinto
    @GetMapping("/solicitudes-guia")
    public List<SolicitudRecintoDto> solicitudes(@AuthenticationPrincipal Jwt jwt) {
        return guiaService.solicitudes(idUsuario(jwt));
    }

    // GET /api/encargado/recinto/solicitudes-guia/5/guias-disponibles → guías libres en ese horario
    @GetMapping("/solicitudes-guia/{idSolicitud}/guias-disponibles")
    public List<GuiaDto> guiasDisponibles(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable("idSolicitud") Long idSolicitud) {
        return guiaService.guiasDisponibles(idUsuario(jwt), idSolicitud);
    }

    // PATCH /api/encargado/recinto/solicitudes-guia/5/asignar  con {"idGuia": 3}
    @PatchMapping("/solicitudes-guia/{idSolicitud}/asignar")
    public SolicitudRecintoDto asignar(@AuthenticationPrincipal Jwt jwt, @PathVariable("idSolicitud") Long idSolicitud,
                                       @Valid @RequestBody AsignarGuiaRequest solicitud) {
        return guiaService.asignar(idUsuario(jwt), idSolicitud, solicitud.idGuia());
    }

    // PATCH /api/encargado/recinto/solicitudes-guia/5/rechazar  con {"motivo": "..."}
    @PatchMapping("/solicitudes-guia/{idSolicitud}/rechazar")
    public SolicitudRecintoDto rechazar(@AuthenticationPrincipal Jwt jwt, @PathVariable("idSolicitud") Long idSolicitud,
                                        @Valid @RequestBody RechazarSolicitudRequest solicitud) {
        return guiaService.rechazar(idUsuario(jwt), idSolicitud, solicitud.motivo());
    }

    // El "sub" del token es el id del usuario (lo pone TokenService al iniciar sesión).
    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
