package cl.duoc.santiago_sacro_api.visita.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** HU-25: lo que se puede cambiar de una visita agendada: fecha, hora y cantidad de personas. */
public record CambioVisitaRequest(
        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La fecha debe ser futura") LocalDateTime inicio,
        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "Debe ir al menos una persona")
        @Max(value = 200, message = "La cantidad máxima es 200 personas") Integer cantidadPersonas) {
}
