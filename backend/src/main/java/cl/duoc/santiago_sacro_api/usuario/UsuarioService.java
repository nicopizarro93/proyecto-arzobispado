package cl.duoc.santiago_sacro_api.usuario;

import cl.duoc.santiago_sacro_api.usuario.dto.UsuarioDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioDto perfil(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .map(UsuarioDto::desde)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}