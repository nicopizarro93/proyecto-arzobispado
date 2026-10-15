package cl.duoc.santiago_sacro_api.encargado;

import cl.duoc.santiago_sacro_api.encargado.dto.CambioEstadoRequest;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioDto;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioRequest;
import org.springframework.http.HttpStatus;

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

    // El "sub" del token es el id del usuario (lo pone TokenService al iniciar sesión).
    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}