package cl.duoc.aurasacrapp_api.auth;

import cl.duoc.aurasacrapp_api.auth.dto.LoginRequest;
import cl.duoc.aurasacrapp_api.auth.dto.LoginResponse;
import cl.duoc.aurasacrapp_api.auth.dto.RegistroRequest;
import cl.duoc.aurasacrapp_api.usuario.dto.UsuarioDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/registro → 201 Created con los datos de la cuenta nueva
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto registrar(@Valid @RequestBody RegistroRequest solicitud) {
        return authService.registrar(solicitud);
    }

    // POST /api/auth/login → 200 con el token
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest solicitud) {
        return authService.login(solicitud);
    }
}
