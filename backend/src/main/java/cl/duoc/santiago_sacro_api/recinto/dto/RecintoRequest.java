package cl.duoc.santiago_sacro_api.recinto.dto;

import cl.duoc.santiago_sacro_api.recinto.TipoRecinto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** HU-35: datos para registrar un recinto en la red. */
public record RecintoRequest(
        @NotNull(message = "El tipo es obligatorio") TipoRecinto tipo,
        @NotBlank(message = "El nombre oficial es obligatorio")
        @Size(max = 150, message = "El nombre admite hasta 150 caracteres") String nombre,
        @NotNull(message = "La comuna es obligatoria") Long idComuna,
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 150, message = "La calle admite hasta 150 caracteres") String calle,
        @Size(max = 10, message = "El número admite hasta 10 caracteres") String numero,
        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90", message = "La latitud debe estar entre -90 y 90")
        @DecimalMax(value = "90", message = "La latitud debe estar entre -90 y 90") BigDecimal latitud,
        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180", message = "La longitud debe estar entre -180 y 180")
        @DecimalMax(value = "180", message = "La longitud debe estar entre -180 y 180") BigDecimal longitud,
        @Size(max = 5000, message = "La reseña admite hasta 5000 caracteres") String descripcionHistorica) {

    public RecintoRequest {
        nombre = nombre == null ? null : nombre.trim();
        calle = calle == null ? null : calle.trim();
        numero = (numero == null || numero.isBlank()) ? null : numero.trim();
        descripcionHistorica = (descripcionHistorica == null || descripcionHistorica.isBlank())
                ? null : descripcionHistorica.trim();
    }
}