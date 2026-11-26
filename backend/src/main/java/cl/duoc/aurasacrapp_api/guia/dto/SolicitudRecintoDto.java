package cl.duoc.aurasacrapp_api.guia.dto;

import cl.duoc.aurasacrapp_api.guia.EstadoSolicitud;
import cl.duoc.aurasacrapp_api.guia.SolicitudGuia;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.visita.Visita;

import java.time.LocalDateTime;

/** Solicitud vista por el encargado (HU-33): fecha, hora y cantidad de personas de la visita. */
public record SolicitudRecintoDto(Long id,
                                  EstadoSolicitud estado,
                                  Long idVisita,
                                  LocalDateTime inicio,
                                  int cantidadPersonas,
                                  String nivelCurso,
                                  String solicitante,
                                  String institucion,
                                  Long idGuia,
                                  String guia,
                                  String motivoRechazo) {

    public static SolicitudRecintoDto desde(SolicitudGuia s) {
        Visita v = s.getVisita();
        Usuario u = v.getUsuario();
        return new SolicitudRecintoDto(
                s.getId(), s.getEstado(), v.getId(), v.getInicio(), v.getCantidadPersonas(), v.getNivelCurso(),
                u.getNombre() + " " + u.getApellido(), u.getInstitucion(),
                s.getGuia() == null ? null : s.getGuia().getId(),
                s.getGuia() == null ? null : s.getGuia().nombreCompleto(),
                s.getMotivoRechazo());
    }
}
