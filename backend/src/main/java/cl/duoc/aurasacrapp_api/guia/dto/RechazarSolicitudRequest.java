package cl.duoc.aurasacrapp_api.guia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** HU-33: para rechazar una solicitud hay que escribir el motivo. */
public record RechazarSolicitudRequest(
        @NotBlank(message = "Escribe el motivo del rechazo")
        @Size(max = 300, message = "El motivo admite hasta 300 caracteres") String motivo) {

    public RechazarSolicitudRequest {
        motivo = motivo == null ? null : motivo.trim();
    }
}
