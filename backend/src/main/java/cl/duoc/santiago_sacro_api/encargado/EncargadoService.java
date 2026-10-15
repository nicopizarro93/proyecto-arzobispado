package cl.duoc.santiago_sacro_api.encargado;

import cl.duoc.santiago_sacro_api.auth.CuentaDeshabilitadaException;
import cl.duoc.santiago_sacro_api.recinto.EstadoApertura;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.RecintoService;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import cl.duoc.santiago_sacro_api.usuario.Rol;
import cl.duoc.santiago_sacro_api.usuario.Usuario;
import cl.duoc.santiago_sacro_api.usuario.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EncargadoService {

    private final UsuarioRepository usuarioRepository;
    private final RecintoService recintoService;

    public EncargadoService(UsuarioRepository usuarioRepository, RecintoService recintoService) {
        this.usuarioRepository = usuarioRepository;
        this.recintoService = recintoService;
    }

    // Ficha del recinto asignado, con su estado y sus horarios.
    @Transactional(readOnly = true)
    public RecintoDetalleDto miRecinto(Long idUsuario) {
        return recintoService.obtener(recintoAsignado(idUsuario).getId());
    }

    // Cambia el estado y guarda la fecha y la hora del cambio (HU-26).
    @Transactional
    public RecintoDetalleDto cambiarEstado(Long idUsuario, EstadoApertura estado) {
        Recinto recinto = recintoAsignado(idUsuario);
        recinto.setEstadoApertura(estado);
        recinto.setFechaEstado(LocalDateTime.now());
        return recintoService.obtener(recinto.getId());
    }

    // El recinto sale de la cuenta del token, nunca de la URL:
    // así un encargado no puede modificar un recinto ajeno (HU-03).
    private Recinto recintoAsignado(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(CuentaDeshabilitadaException::new);
        if (usuario.getRol() != Rol.ENCARGADO) {
            throw new AccessDeniedException("La cuenta no es de un encargado");
        }
        return usuario.getRecinto();
    }
}