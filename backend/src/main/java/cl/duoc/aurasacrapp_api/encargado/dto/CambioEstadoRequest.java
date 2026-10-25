package cl.duoc.aurasacrapp_api.encargado.dto;

import cl.duoc.aurasacrapp_api.recinto.EstadoApertura;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio") EstadoApertura estado) {
}