package cl.duoc.santiago_sacro_api.encargado;

import cl.duoc.santiago_sacro_api.encargado.dto.CambioEstadoRequest;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    // El "sub" del token es el id del usuario (lo pone TokenService al iniciar sesión).
    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}