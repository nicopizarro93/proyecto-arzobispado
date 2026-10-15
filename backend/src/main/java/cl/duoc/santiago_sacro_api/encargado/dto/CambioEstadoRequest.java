package cl.duoc.santiago_sacro_api.encargado.dto;

import cl.duoc.santiago_sacro_api.recinto.EstadoApertura;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio") EstadoApertura estado) {
}