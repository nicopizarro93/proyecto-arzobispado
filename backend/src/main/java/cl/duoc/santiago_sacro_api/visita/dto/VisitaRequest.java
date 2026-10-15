package cl.duoc.santiago_sacro_api.visita.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/** Datos para agendar o editar una visita. El destino es un recinto o una ruta (HU-21, HU-22). */
public record VisitaRequest(
        Long idRecinto,
        Long idRuta,
        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La fecha debe ser futura") LocalDateTime inicio,
        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "Debe ir al menos una persona")
        @Max(value = 200, message = "La cantidad máxima es 200 personas") Integer cantidadPersonas,
        @Size(max = 50, message = "El curso o nivel admite hasta 50 caracteres") String nivelCurso,
        @Size(max = 500, message = "Las observaciones admiten hasta 500 caracteres") String observaciones) {

    public VisitaRequest {
        nivelCurso = (nivelCurso == null || nivelCurso.isBlank()) ? null : nivelCurso.trim();
        observaciones = (observaciones == null || observaciones.isBlank()) ? null : observaciones.trim();
    }

    // Se agenda a un recinto o a una ruta, nunca a los dos ni a ninguno.
    @AssertTrue(message = "Indica un recinto o una ruta, no los dos")
    public boolean isDestinoUnico() {
        return (idRecinto == null) != (idRuta == null);
    }
}