package cl.duoc.aurasacrapp_api.horario.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/** Datos para crear o editar un bloque de horario. Las horas van como "09:00". */
public record HorarioRequest(
        @NotNull(message = "El día es obligatorio")
        @Min(value = 1, message = "El día debe estar entre 1 (lunes) y 7 (domingo)")
        @Max(value = 7, message = "El día debe estar entre 1 (lunes) y 7 (domingo)")
        Integer diaSemana,
        @NotNull(message = "La hora de apertura es obligatoria") LocalTime horaApertura,
        @NotNull(message = "La hora de cierre es obligatoria") LocalTime horaCierre) {

    // Si falta una hora ya lo informa @NotNull; aquí solo se comparan cuando vienen las dos.
    @AssertTrue(message = "La hora de cierre debe ser posterior a la de apertura")
    public boolean isRangoValido() {
        return horaApertura == null || horaCierre == null || horaCierre.isAfter(horaApertura);
    }
}
