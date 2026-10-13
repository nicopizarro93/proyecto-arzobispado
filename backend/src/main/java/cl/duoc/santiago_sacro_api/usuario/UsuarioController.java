package cl.duoc.santiago_sacro_api.usuario;

import cl.duoc.santiago_sacro_api.usuario.dto.UsuarioDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
