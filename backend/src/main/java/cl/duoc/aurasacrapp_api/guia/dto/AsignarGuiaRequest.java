package cl.duoc.aurasacrapp_api.guia.dto;

import jakarta.validation.constraints.NotNull;

/** HU-33: el guía que el encargado asigna a una solicitud. */
public record AsignarGuiaRequest(@NotNull(message = "El guía es obligatorio") Long idGuia) {
}
