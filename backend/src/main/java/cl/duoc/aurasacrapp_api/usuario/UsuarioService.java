package cl.duoc.aurasacrapp_api.usuario;

import cl.duoc.aurasacrapp_api.usuario.dto.UsuarioDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.aurasacrapp_api.usuario.dto.CambioPasswordRequest;
import cl.duoc.aurasacrapp_api.usuario.dto.PerfilRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UsuarioDto perfil(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .map(UsuarioDto::desde)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // HU-05: nombre, apellido e institución (esta última solo para docentes). El correo no cambia.
    @Transactional
    public UsuarioDto actualizarPerfil(Long idUsuario, PerfilRequest datos) {
        Usuario usuario = cuentaActiva(idUsuario);
        usuario.setNombre(datos.nombre());
        usuario.setApellido(datos.apellido());
        if (usuario.getRol() == Rol.DOCENTE) {
            if (datos.institucion() == null || datos.institucion().length() < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Como docente, indica tu colegio o institución (mínimo 2 caracteres)");
            }
            usuario.setInstitucion(datos.institucion());
        }
        return UsuarioDto.desde(usuario);
    }

    // HU-05: pide la contraseña actual; la nueva se guarda cifrada con BCrypt, igual que en el registro.
    @Transactional
    public void cambiarPassword(Long idUsuario, CambioPasswordRequest datos) {
        Usuario usuario = cuentaActiva(idUsuario);
        if (!passwordEncoder.matches(datos.passwordActual(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        usuario.setPasswordHash(passwordEncoder.encode(datos.passwordNueva()));
    }

    private Usuario cuentaActiva(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }    
}