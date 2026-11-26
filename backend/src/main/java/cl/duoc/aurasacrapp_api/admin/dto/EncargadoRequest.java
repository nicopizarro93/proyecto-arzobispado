package cl.duoc.aurasacrapp_api.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

/** HU-34: datos para crear la cuenta de un encargado. La clave inicial la elige el administrador. */
public record EncargadoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres") String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres") String apellido,
        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$", message = "El correo no tiene un formato válido") String correo,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "La contraseña debe tener al menos una letra y un número") String password,
        @NotNull(message = "El recinto es obligatorio") Long idRecinto) {

    // Quita espacios y deja el correo en minúsculas, igual que en el registro.
    public EncargadoRequest {
        nombre = nombre == null ? null : nombre.trim();
        apellido = apellido == null ? null : apellido.trim();
        correo = correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }
}
