package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.auth.CuentaDeshabilitadaException;
import cl.duoc.aurasacrapp_api.guia.dto.DisponibilidadDto;
import cl.duoc.aurasacrapp_api.guia.dto.DisponibilidadRequest;
import cl.duoc.aurasacrapp_api.guia.dto.GuiaDto;
import cl.duoc.aurasacrapp_api.guia.dto.GuiaRequest;
import cl.duoc.aurasacrapp_api.guia.dto.SolicitudRecintoDto;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import cl.duoc.aurasacrapp_api.visita.EstadoVisita;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lo que el encargado hace con los guías de su recinto: administrarlos y su disponibilidad (HU-32)
 * y resolver las solicitudes que le llegan (HU-33). El recinto sale de la cuenta del token,
 * nunca de la URL: un encargado no puede ver ni tocar los guías de otro recinto.
 */
@Service
public class GuiaEncargadoService {

    private final UsuarioRepository usuarioRepository;
    private final GuiaRepository guiaRepository;
    private final DisponibilidadGuiaRepository disponibilidadRepository;
    private final SolicitudGuiaRepository solicitudRepository;

    public GuiaEncargadoService(UsuarioRepository usuarioRepository,
                                GuiaRepository guiaRepository,
                                DisponibilidadGuiaRepository disponibilidadRepository,
                                SolicitudGuiaRepository solicitudRepository) {
        this.usuarioRepository = usuarioRepository;
        this.guiaRepository = guiaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.solicitudRepository = solicitudRepository;
    }

    // ---- Guías (HU-32) ----

    @Transactional(readOnly = true)
    public List<GuiaDto> guias(Long idUsuario) {
        Recinto recinto = recintoAsignado(idUsuario);
        return guiaRepository.findByRecintoIdOrderByNombreAscApellidoAsc(recinto.getId()).stream()
                .map(this::aDto)
                .toList();
    }

    @Transactional
    public GuiaDto crearGuia(Long idUsuario, GuiaRequest datos) {
        Guia guia = new Guia();
        guia.setRecinto(recintoAsignado(idUsuario));
        guia.setNombre(datos.nombre());
        guia.setApellido(datos.apellido());
        return aDto(guiaRepository.save(guia));
    }

    @Transactional
    public GuiaDto editarGuia(Long idUsuario, Long idGuia, GuiaRequest datos) {
        Guia guia = guiaDelRecinto(idUsuario, idGuia);
        guia.setNombre(datos.nombre());
        guia.setApellido(datos.apellido());
        return aDto(guia);
    }

    // Un guía desactivado no se puede asignar, pero queda en el historial.
    @Transactional
    public GuiaDto cambiarActivo(Long idUsuario, Long idGuia, boolean activo) {
        Guia guia = guiaDelRecinto(idUsuario, idGuia);
        guia.setActivo(activo);
        return aDto(guia);
    }

    // ---- Disponibilidad (HU-32) ----

    @Transactional
    public GuiaDto agregarDisponibilidad(Long idUsuario, Long idGuia, DisponibilidadRequest datos) {
        Guia guia = guiaDelRecinto(idUsuario, idGuia);
        if (!disponibilidadRepository
                .findByGuiaIdAndInicioLessThanAndFinGreaterThan(idGuia, datos.fin(), datos.inicio()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El bloque se cruza con otra disponibilidad del guía.");
        }
        DisponibilidadGuia bloque = new DisponibilidadGuia();
        bloque.setGuia(guia);
        bloque.setInicio(datos.inicio());
        bloque.setFin(datos.fin());
        disponibilidadRepository.save(bloque);
        return aDto(guia);
    }

    @Transactional
    public GuiaDto eliminarDisponibilidad(Long idUsuario, Long idGuia, Long idDisponibilidad) {
        Guia guia = guiaDelRecinto(idUsuario, idGuia);
        DisponibilidadGuia bloque = disponibilidadRepository.findByIdAndGuiaId(idDisponibilidad, idGuia)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el bloque " + idDisponibilidad + " de ese guía"));
        disponibilidadRepository.delete(bloque);
        return aDto(guia);
    }

    // ---- Solicitudes (HU-33) ----

    @Transactional(readOnly = true)
    public List<SolicitudRecintoDto> solicitudes(Long idUsuario) {
        Recinto recinto = recintoAsignado(idUsuario);
        return solicitudRepository.buscarDelRecinto(recinto.getId()).stream()
                .map(SolicitudRecintoDto::desde)
                .toList();
    }

    // La app sugiere los guías activos que están disponibles en el horario de la visita y libres.
    @Transactional(readOnly = true)
    public List<GuiaDto> guiasDisponibles(Long idUsuario, Long idSolicitud) {
        Recinto recinto = recintoAsignado(idUsuario);
        SolicitudGuia solicitud = solicitudDelRecinto(recinto, idSolicitud);
        LocalDateTime inicio = solicitud.getVisita().getInicio();
        return guiaRepository.findByRecintoIdOrderByNombreAscApellidoAsc(recinto.getId()).stream()
                .filter(g -> Boolean.TRUE.equals(g.getActivo()))
                .filter(g -> estaDisponible(g.getId(), inicio, idSolicitud))
                .map(this::aDto)
                .toList();
    }

    @Transactional
    public SolicitudRecintoDto asignar(Long idUsuario, Long idSolicitud, Long idGuia) {
        Recinto recinto = recintoAsignado(idUsuario);
        SolicitudGuia solicitud = solicitudDelRecinto(recinto, idSolicitud);
        exigirPendiente(solicitud);

        Guia guia = guiaRepository.findByIdAndRecintoId(idGuia, recinto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el guía " + idGuia + " en tu recinto"));
        if (!Boolean.TRUE.equals(guia.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El guía está desactivado.");
        }
        LocalDateTime inicio = solicitud.getVisita().getInicio();
        if (!disponibilidadRepository.existsByGuiaIdAndInicioLessThanEqualAndFinGreaterThanEqual(idGuia, inicio, inicio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El guía no está disponible a esa hora.");
        }
        if (solicitudRepository.guiaOcupado(idGuia, inicio, idSolicitud)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El guía ya está asignado a otra visita a esa hora.");
        }

        solicitud.setGuia(guia);
        solicitud.setEstado(EstadoSolicitud.ASIGNADA);
        solicitud.setResueltaPor(usuarioRepository.getReferenceById(idUsuario));
        solicitud.setFechaResolucion(LocalDateTime.now());
        return SolicitudRecintoDto.desde(solicitud);
    }

    @Transactional
    public SolicitudRecintoDto rechazar(Long idUsuario, Long idSolicitud, String motivo) {
        Recinto recinto = recintoAsignado(idUsuario);
        SolicitudGuia solicitud = solicitudDelRecinto(recinto, idSolicitud);
        exigirPendiente(solicitud);

        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitud.setResueltaPor(usuarioRepository.getReferenceById(idUsuario));
        solicitud.setFechaResolucion(LocalDateTime.now());
        return SolicitudRecintoDto.desde(solicitud);
    }

    // ---- Apoyo ----

    private void exigirPendiente(SolicitudGuia solicitud) {
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya fue resuelta o cancelada.");
        }
        if (solicitud.getVisita().getEstado() != EstadoVisita.AGENDADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La visita ya se realizó o fue cancelada.");
        }
    }

    private boolean estaDisponible(Long idGuia, LocalDateTime inicio, Long idSolicitud) {
        return disponibilidadRepository.existsByGuiaIdAndInicioLessThanEqualAndFinGreaterThanEqual(idGuia, inicio, inicio)
                && !solicitudRepository.guiaOcupado(idGuia, inicio, idSolicitud);
    }

    // Una solicitud de otro recinto responde 404, igual que una que no existe.
    private SolicitudGuia solicitudDelRecinto(Recinto recinto, Long idSolicitud) {
        return solicitudRepository.buscarUnaDelRecinto(idSolicitud, recinto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe la solicitud " + idSolicitud + " en tu recinto"));
    }

    private Guia guiaDelRecinto(Long idUsuario, Long idGuia) {
        Recinto recinto = recintoAsignado(idUsuario);
        return guiaRepository.findByIdAndRecintoId(idGuia, recinto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el guía " + idGuia + " en tu recinto"));
    }

    private GuiaDto aDto(Guia guia) {
        List<DisponibilidadDto> bloques = disponibilidadRepository.findByGuiaIdOrderByInicioAsc(guia.getId()).stream()
                .map(DisponibilidadDto::desde)
                .toList();
        return GuiaDto.desde(guia, bloques);
    }

    // El recinto sale de la cuenta del token, nunca de la URL (HU-03).
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
