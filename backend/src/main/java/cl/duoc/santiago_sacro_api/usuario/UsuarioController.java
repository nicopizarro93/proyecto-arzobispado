package cl.duoc.santiago_sacro_api.usuario;

import cl.duoc.santiago_sacro_api.usuario.dto.UsuarioDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import cl.duoc.santiago_sacro_api.usuario.dto.CambioPasswordRequest;
import cl.duoc.santiago_sacro_api.usuario.dto.PerfilRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios/yo (requiere token): datos de quien inició sesión.
    @GetMapping("/yo")
    public UsuarioDto yo(@AuthenticationPrincipal Jwt jwt) {
        return usuarioService.perfil(Long.valueOf(jwt.getSubject()));
    }

    // PUT /api/usuarios/yo → actualiza nombre, apellido e institución
    @PutMapping("/yo")
    public UsuarioDto actualizarPerfil(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody PerfilRequest solicitud) {
        return usuarioService.actualizarPerfil(Long.valueOf(jwt.getSubject()), solicitud);
    }

    // PUT /api/usuarios/yo/password → 204 si la contraseña se cambió
    @PutMapping("/yo/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(@AuthenticationPrincipal Jwt jwt,
                                @Valid @RequestBody CambioPasswordRequest solicitud) {
        usuarioService.cambiarPassword(Long.valueOf(jwt.getSubject()), solicitud);
    }    
}
