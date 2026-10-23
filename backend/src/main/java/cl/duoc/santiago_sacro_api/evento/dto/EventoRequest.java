package cl.duoc.santiago_sacro_api.evento.dto;

import cl.duoc.santiago_sacro_api.evento.TipoEvento;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Datos para publicar o editar un evento, aviso, apertura o cierre. Fechas como "2026-10-25T19:00". */
public record EventoRequest(
        @NotNull(message = "El tipo es obligatorio") TipoEvento tipo,
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título admite hasta 150 caracteres") String titulo,
        @Size(max = 2000, message = "La descripción admite hasta 2000 caracteres") String descripcion,
        @NotNull(message = "La fecha y hora de inicio es obligatoria") LocalDateTime inicio,
        LocalDateTime fin) {

    public EventoRequest {
        titulo = titulo == null ? null : titulo.trim();
        descripcion = (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    }

    // HU-28 y HU-30: si viene el término, debe ser posterior al inicio.
    @AssertTrue(message = "El término debe ser posterior al inicio")
    public boolean isFinPosteriorAlInicio() {
        return inicio == null || fin == null || fin.isAfter(inicio);
    }

    // HU-30: en una apertura o un cierre excepcional el término es obligatorio.
    @AssertTrue(message = "Las aperturas y los cierres excepcionales requieren la hora de término")
    public boolean isFinPresenteEnExcepciones() {
        return tipo == null || !tipo.esExcepcionDeHorario() || fin != null;
    }
}