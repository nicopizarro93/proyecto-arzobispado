package cl.duoc.aurasacrapp_api.encargado.dto;

import jakarta.validation.constraints.Size;

/** HU-31: lo que el encargado puede editar de la ficha de su recinto: la reseña histórica. */
public record FichaRequest(
        @Size(max = 5000, message = "La reseña admite hasta 5000 caracteres") String descripcionHistorica) {

    // Una reseña vacía se guarda como "sin reseña" (null).
    public FichaRequest {
        descripcionHistorica = (descripcionHistorica == null || descripcionHistorica.isBlank())
                ? null : descripcionHistorica.trim();
    }
}
