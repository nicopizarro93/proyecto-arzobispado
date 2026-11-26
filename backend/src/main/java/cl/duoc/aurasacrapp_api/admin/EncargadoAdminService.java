package cl.duoc.aurasacrapp_api.admin;

import cl.duoc.aurasacrapp_api.admin.dto.EncargadoDto;
import cl.duoc.aurasacrapp_api.admin.dto.EncargadoRequest;
import cl.duoc.aurasacrapp_api.auth.CorreoYaRegistradoException;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.RecintoRepository;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gestión de las cuentas de encargados por el administrador de la red (HU-34).
 * Un encargado no se registra por su cuenta: la crea el administrador y la asocia a un recinto.
 */
@Service
public class EncargadoAdminService {

    private final UsuarioRepository usuarioRepository;
    private final RecintoRepository recintoRepository;
    private final PasswordEncoder passwordEncoder;

    public EncargadoAdminService(UsuarioRepository usuarioRepository,
                                 RecintoRepository recintoRepository,
                                 PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.recintoRepository = recintoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<EncargadoDto> listar() {
        return usuarioRepository.findByRolOrderByApellidoAscNombreAsc(Rol.ENCARGADO).stream()
                .map(EncargadoDto::desde)
                .toList();
    }

    @Transactional
    public EncargadoDto crear(EncargadoRequest datos) {
        if (usuarioRepository.existsByCorreo(datos.correo())) {
            throw new CorreoYaRegistradoException(datos.correo());
        }
        Recinto recinto = recintoRepository.buscarActivoPorId(datos.idRecinto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un recinto activo con id " + datos.idRecinto()));
        exigirRecintoSinEncargado(recinto.getId());

        Usuario usuario = new Usuario();
        usuario.setRol(Rol.ENCARGADO);
        usuario.setRecinto(recinto);
        usuario.setNombre(datos.nombre());
        usuario.setApellido(datos.apellido());
        usuario.setCorreo(datos.correo());
        usuario.setPasswordHash(passwordEncoder.encode(datos.password()));
        usuario.setFechaAceptaPrivacidad(LocalDateTime.now());
        return EncargadoDto.desde(usuarioRepository.save(usuario));
    }

    // Desactivar una cuenta le impide iniciar sesión; activarla de nuevo exige que el recinto
    // no tenga ya otro encargado activo.
    @Transactional
    public EncargadoDto cambiarActivo(Long idUsuario, boolean activo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .filter(u -> u.getRol() == Rol.ENCARGADO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un encargado con id " + idUsuario));
        if (activo && !Boolean.TRUE.equals(usuario.getActivo())) {
            exigirRecintoSinEncargado(usuario.getRecinto().getId());
        }
        usuario.setActivo(activo);
        return EncargadoDto.desde(usuario);
    }

    private void exigirRecintoSinEncargado(Long idRecinto) {
        if (usuarioRepository.existsByRolAndRecintoIdAndActivoTrue(Rol.ENCARGADO, idRecinto)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese recinto ya tiene un encargado activo");
        }
    }
}
