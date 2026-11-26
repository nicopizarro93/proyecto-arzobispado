package cl.duoc.aurasacrapp_api.guia.dto;

import jakarta.validation.constraints.NotNull;

/** HU-23: el recinto de la visita para el que se pide un guía. */
public record SolicitudGuiaRequest(@NotNull(message = "El recinto es obligatorio") Long idRecinto) {
}
