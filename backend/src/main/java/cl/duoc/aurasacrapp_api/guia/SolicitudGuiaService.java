package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.guia.dto.SolicitudUsuarioDto;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.RecintoRepository;
import cl.duoc.aurasacrapp_api.visita.EstadoVisita;
import cl.duoc.aurasacrapp_api.visita.Visita;
import cl.duoc.aurasacrapp_api.visita.VisitaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/** Lo que hacen el visitante registrado y el docente con los guías: pedirlos (HU-23) y ver su estado (HU-24). */
@Service
public class SolicitudGuiaService {

    private final SolicitudGuiaRepository solicitudRepository;
    private final VisitaRepository visitaRepository;
    private final RecintoRepository recintoRepository;

    public SolicitudGuiaService(SolicitudGuiaRepository solicitudRepository,
                                VisitaRepository visitaRepository,
                                RecintoRepository recintoRepository) {
        this.solicitudRepository = solicitudRepository;
        this.visitaRepository = visitaRepository;
        this.recintoRepository = recintoRepository;
    }

    // HU-23: se crea una solicitud "Pendiente" por cada recinto, que recibe el encargado de ese recinto.
    @Transactional
    public SolicitudUsuarioDto solicitar(Long idUsuario, Long idVisita, Long idRecinto) {
        Visita visita = visitaRepository.buscarUnaDelUsuario(idVisita, idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No tienes una visita con id " + idVisita));
        if (visita.getEstado() != EstadoVisita.AGENDADA || !visita.getInicio().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La visita ya se realizó o fue cancelada");
        }
        if (!recintoEnLaVisita(visita, idRecinto)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ese recinto no es parte de tu visita");
        }
        Recinto recinto = recintoRepository.buscarActivoPorId(idRecinto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un recinto activo con id " + idRecinto));
        if (solicitudRepository.existsByVisitaIdAndRecintoId(idVisita, idRecinto)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya pediste un guía para este recinto en esta visita");
        }

        SolicitudGuia solicitud = new SolicitudGuia();
        solicitud.setVisita(visita);
        solicitud.setRecinto(recinto);
        return SolicitudUsuarioDto.desde(solicitudRepository.save(solicitud));
    }

    // HU-24: todas mis solicitudes, con su estado, el guía asignado o el motivo del rechazo.
    @Transactional(readOnly = true)
    public List<SolicitudUsuarioDto> misSolicitudes(Long idUsuario) {
        return solicitudRepository.buscarDelUsuario(idUsuario).stream()
                .map(SolicitudUsuarioDto::desde)
                .toList();
    }

    // El recinto es el de la visita o, si la visita es a una ruta, una de sus paradas.
    private boolean recintoEnLaVisita(Visita visita, Long idRecinto) {
        if (visita.getRecinto() != null) {
            return visita.getRecinto().getId().equals(idRecinto);
        }
        return visita.getRuta().getParadas().stream()
                .anyMatch(parada -> parada.getRecinto().getId().equals(idRecinto));
    }
}
