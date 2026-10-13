package cl.duoc.santiago_sacro_api.auth;

import cl.duoc.santiago_sacro_api.auth.dto.RegistroRequest;
import cl.duoc.santiago_sacro_api.usuario.Rol;
import cl.duoc.santiago_sacro_api.usuario.Usuario;
import cl.duoc.santiago_sacro_api.usuario.UsuarioRepository;
import cl.duoc.santiago_sacro_api.usuario.dto.UsuarioDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
}
