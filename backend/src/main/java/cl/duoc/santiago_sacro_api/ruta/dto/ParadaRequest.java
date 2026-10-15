package cl.duoc.santiago_sacro_api.ruta.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Una parada pedida por la app: el recinto y los minutos de visita (30 si no se indican). */
public record ParadaRequest(
        @NotNull(message = "El recinto es obligatorio") Long idRecinto,
        @Min(value = 5, message = "La visita dura al menos 5 minutos")
        @Max(value = 480, message = "La visita dura como máximo 480 minutos") Integer tiempoVisitaMin) {

    public static final int MINUTOS_POR_DEFECTO = 30;

    public ParadaRequest {
        tiempoVisitaMin = tiempoVisitaMin == null ? MINUTOS_POR_DEFECTO : tiempoVisitaMin;
    }
}
