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
import cl.duoc.santiago_sacro_api.horario.HorarioService;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioDto;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioRequest;
import java.time.LocalDateTime;
import cl.duoc.santiago_sacro_api.evento.EventoService;
import cl.duoc.santiago_sacro_api.evento.dto.EventoGestionDto;
import cl.duoc.santiago_sacro_api.evento.dto.EventoRequest;

import java.util.List;

@Service
public class EncargadoService {

    private final UsuarioRepository usuarioRepository;
    private final RecintoService recintoService;
    private final HorarioService horarioService;
    private final EventoService eventoService;

    public EncargadoService(UsuarioRepository usuarioRepository, RecintoService recintoService,
                            HorarioService horarioService, EventoService eventoService) {
        this.usuarioRepository = usuarioRepository;
        this.recintoService = recintoService;
        this.horarioService = horarioService;
        this.eventoService = eventoService;
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

    // Horarios del recinto asignado (HU-27).
    @Transactional
    public HorarioDto agregarHorario(Long idUsuario, HorarioRequest datos) {
        return horarioService.crear(recintoAsignado(idUsuario), datos);
    }

    @Transactional
    public HorarioDto editarHorario(Long idUsuario, Long idHorario, HorarioRequest datos) {
        return horarioService.actualizar(recintoAsignado(idUsuario), idHorario, datos);
    }

    @Transactional
    public void eliminarHorario(Long idUsuario, Long idHorario) {
        horarioService.eliminar(recintoAsignado(idUsuario), idHorario);
    }

    // Eventos, avisos, aperturas y cierres del recinto asignado (HU-28 a HU-30).
    @Transactional(readOnly = true)
    public List<EventoGestionDto> historialEventos(Long idUsuario) {
        return eventoService.historialDelRecinto(recintoAsignado(idUsuario).getId());
    }

    @Transactional
    public EventoGestionDto publicarEvento(Long idUsuario, EventoRequest datos) {
        Recinto recinto = recintoAsignado(idUsuario);
        // La cuenta ya se validó en recintoAsignado; aquí solo se toma su referencia para "publicado por".
        Usuario encargado = usuarioRepository.getReferenceById(idUsuario);
        return eventoService.publicar(recinto, encargado, datos);
    }

    @Transactional
    public EventoGestionDto editarEvento(Long idUsuario, Long idEvento, EventoRequest datos) {
        return eventoService.editar(recintoAsignado(idUsuario), idEvento, datos);
    }

    @Transactional
    public EventoGestionDto cancelarEvento(Long idUsuario, Long idEvento) {
        return eventoService.cancelar(recintoAsignado(idUsuario), idEvento);
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