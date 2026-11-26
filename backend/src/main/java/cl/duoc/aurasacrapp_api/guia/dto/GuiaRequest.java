package cl.duoc.aurasacrapp_api.guia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** HU-32: datos para registrar o editar un guía voluntario. */
public record GuiaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres") String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres") String apellido) {

    public GuiaRequest {
        nombre = nombre == null ? null : nombre.trim();
        apellido = apellido == null ? null : apellido.trim();
    }
}
