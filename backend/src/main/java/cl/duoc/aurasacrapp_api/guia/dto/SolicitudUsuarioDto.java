package cl.duoc.aurasacrapp_api.guia.dto;

import cl.duoc.aurasacrapp_api.guia.EstadoSolicitud;
import cl.duoc.aurasacrapp_api.guia.SolicitudGuia;

/** Solicitud vista por quien la pidió (HU-24): estado, guía asignado o motivo del rechazo. */
public record SolicitudUsuarioDto(Long id,
                                  Long idVisita,
                                  Long idRecinto,
                                  String recinto,
                                  EstadoSolicitud estado,
                                  String guia,
                                  String motivoRechazo) {

    public static SolicitudUsuarioDto desde(SolicitudGuia s) {
        return new SolicitudUsuarioDto(
                s.getId(), s.getVisita().getId(), s.getRecinto().getId(), s.getRecinto().getNombreOficial(),
                s.getEstado(),
                s.getGuia() == null ? null : s.getGuia().nombreCompleto(),
                s.getMotivoRechazo());
    }
}
