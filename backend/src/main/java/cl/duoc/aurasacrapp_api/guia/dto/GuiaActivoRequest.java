package cl.duoc.aurasacrapp_api.guia.dto;

import jakarta.validation.constraints.NotNull;

/** HU-32: activar o desactivar a un guía. */
public record GuiaActivoRequest(@NotNull(message = "Indica si el guía está activo") Boolean activo) {
}
