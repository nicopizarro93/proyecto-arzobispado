package cl.duoc.aurasacrapp_api.admin.dto;

import jakarta.validation.constraints.NotNull;

/** HU-34: activar o desactivar una cuenta. */
public record CuentaActivaRequest(@NotNull(message = "Indica si la cuenta está activa") Boolean activo) {
}
