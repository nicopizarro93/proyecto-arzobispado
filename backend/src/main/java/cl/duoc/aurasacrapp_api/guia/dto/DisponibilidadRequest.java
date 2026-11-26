package cl.duoc.aurasacrapp_api.guia.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** HU-32: bloque de disponibilidad de un guía. Fechas como "2026-12-05T10:00". */
public record DisponibilidadRequest(
        @NotNull(message = "El inicio es obligatorio") LocalDateTime inicio,
        @NotNull(message = "El término es obligatorio") LocalDateTime fin) {

    @AssertTrue(message = "El término debe ser posterior al inicio")
    public boolean isFinPosteriorAlInicio() {
        return inicio == null || fin == null || fin.isAfter(inicio);
    }
}
