package cl.duoc.aurasacrapp_api.auth;

import cl.duoc.aurasacrapp_api.auth.dto.LoginRequest;
import cl.duoc.aurasacrapp_api.auth.dto.LoginResponse;
import cl.duoc.aurasacrapp_api.auth.dto.RegistroRequest;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import cl.duoc.aurasacrapp_api.usuario.dto.UsuarioDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final int MAX_INTENTOS = 5;
    private static final Duration DURACION_BLOQUEO = Duration.ofMinutes(1);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public UsuarioDto registrar(RegistroRequest solicitud) {
        if (usuarioRepository.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaRegistradoException(solicitud.correo());
        }
        Usuario usuario = new Usuario();
        usuario.setRol(solicitud.rol());
        usuario.setNombre(solicitud.nombre());
        usuario.setApellido(solicitud.apellido());
        usuario.setCorreo(solicitud.correo());
        usuario.setPasswordHash(passwordEncoder.encode(solicitud.password()));
        // La base solo admite institución en cuentas de docente.
        usuario.setInstitucion(solicitud.rol() == Rol.DOCENTE ? solicitud.institucion() : null);
        usuario.setFechaAceptaPrivacidad(LocalDateTime.now());
        return UsuarioDto.desde(usuarioRepository.save(usuario));
    }

    // noRollbackFor: aunque el login falle, el contador de intentos y el bloqueo SÍ deben guardarse.
    @Transactional(noRollbackFor = {CredencialesInvalidasException.class, CuentaBloqueadaException.class})
    public LoginResponse login(LoginRequest solicitud) {
        // Si el correo no existe se da el mismo error que con una clave incorrecta (no se revela cuál falló).
        Usuario usuario = usuarioRepository.findByCorreo(solicitud.correo())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CuentaDeshabilitadaException();
        }
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(ahora)) {
            throw new CuentaBloqueadaException();
        }

        if (!passwordEncoder.matches(solicitud.password(), usuario.getPasswordHash())) {
            int intentos = usuario.getIntentosFallidos() + 1;
            if (intentos >= MAX_INTENTOS) {
                usuario.setIntentosFallidos(0);
                usuario.setBloqueadoHasta(ahora.plus(DURACION_BLOQUEO));
                throw new CuentaBloqueadaException();
            }
            usuario.setIntentosFallidos(intentos);
            throw new CredencialesInvalidasException();
        }

        // Login correcto: se reinicia el contador y se entrega el token.
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        TokenGenerado token = tokenService.generar(usuario);
        return new LoginResponse(token.valor(), "Bearer", token.expiraEn(), UsuarioDto.desde(usuario));
    }
}