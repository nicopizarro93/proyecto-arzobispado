package cl.duoc.aurasacrapp_api.encargado;

import cl.duoc.aurasacrapp_api.encargado.dto.CambioEstadoRequest;
import cl.duoc.aurasacrapp_api.recinto.dto.RecintoDetalleDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import cl.duoc.aurasacrapp_api.horario.dto.HorarioDto;
import cl.duoc.aurasacrapp_api.horario.dto.HorarioRequest;
import org.springframework.http.HttpStatus;
import cl.duoc.aurasacrapp_api.evento.dto.EventoGestionDto;
import cl.duoc.aurasacrapp_api.evento.dto.EventoRequest;

import java.util.List;

@RestController
@RequestMapping("/api/encargado/recinto")
public class EncargadoController {

    private final EncargadoService encargadoService;

    public EncargadoController(EncargadoService encargadoService) {
        this.encargadoService = encargadoService;
    }

    // GET /api/encargado/recinto → ficha del recinto que tengo asignado
    @GetMapping
    public RecintoDetalleDto miRecinto(@AuthenticationPrincipal Jwt jwt) {
        return encargadoService.miRecinto(idUsuario(jwt));
    }

    // PATCH /api/encargado/recinto/estado  con {"estado": "ABIERTO"}
    @PatchMapping("/estado")
    public RecintoDetalleDto cambiarEstado(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody CambioEstadoRequest solicitud) {
        return encargadoService.cambiarEstado(idUsuario(jwt), solicitud.estado());
    }

    // POST /api/encargado/recinto/horarios → 201 con el bloque creado
    @PostMapping("/horarios")
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioDto agregarHorario(@AuthenticationPrincipal Jwt jwt,
                                     @Valid @RequestBody HorarioRequest solicitud) {
        return encargadoService.agregarHorario(idUsuario(jwt), solicitud);
    }

    // PUT /api/encargado/recinto/horarios/5 → 200 con el bloque modificado
    @PutMapping("/horarios/{idHorario}")
    public HorarioDto editarHorario(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable("idHorario") Long idHorario,
                                    @Valid @RequestBody HorarioRequest solicitud) {
        return encargadoService.editarHorario(idUsuario(jwt), idHorario, solicitud);
    }

    // DELETE /api/encargado/recinto/horarios/5 → 204 sin contenido
    @DeleteMapping("/horarios/{idHorario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarHorario(@AuthenticationPrincipal Jwt jwt,
                                @PathVariable("idHorario") Long idHorario) {
        encargadoService.eliminarHorario(idUsuario(jwt), idHorario);
    }

    // GET /api/encargado/recinto/eventos → historial, incluidos los cancelados
    @GetMapping("/eventos")
    public List<EventoGestionDto> historialEventos(@AuthenticationPrincipal Jwt jwt) {
        return encargadoService.historialEventos(idUsuario(jwt));
    }

    // POST /api/encargado/recinto/eventos → 201 con el evento publicado
    @PostMapping("/eventos")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoGestionDto publicarEvento(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody EventoRequest solicitud) {
        return encargadoService.publicarEvento(idUsuario(jwt), solicitud);
    }

    // PUT /api/encargado/recinto/eventos/12 → 200 con el evento modificado
    @PutMapping("/eventos/{idEvento}")
    public EventoGestionDto editarEvento(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable("idEvento") Long idEvento,
                                         @Valid @RequestBody EventoRequest solicitud) {
        return encargadoService.editarEvento(idUsuario(jwt), idEvento, solicitud);
    }

    // PATCH /api/encargado/recinto/eventos/12/cancelar → 200 con el estado CANCELADO
    @PatchMapping("/eventos/{idEvento}/cancelar")
    public EventoGestionDto cancelarEvento(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("idEvento") Long idEvento) {
        return encargadoService.cancelarEvento(idUsuario(jwt), idEvento);
    }    

    // El "sub" del token es el id del usuario (lo pone TokenService al iniciar sesión).
    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}