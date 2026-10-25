package cl.duoc.aurasacrapp_api.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** HU-05: datos del perfil que se pueden cambiar. El correo no se cambia. */
public record PerfilRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres") String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres") String apellido,
        @Size(max = 120, message = "La institución no puede superar 120 caracteres") String institucion) {

    public PerfilRequest {
        nombre = nombre == null ? null : nombre.trim();
        apellido = apellido == null ? null : apellido.trim();
        institucion = institucion == null ? null : institucion.trim();
    }
}
