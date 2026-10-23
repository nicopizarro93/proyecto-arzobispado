package cl.duoc.santiago_sacro_api.usuario.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** HU-05: la contraseña actual y dos veces la nueva, con las mismas reglas del registro. */
public record CambioPasswordRequest(
        @NotBlank(message = "Escribe tu contraseña actual") String passwordActual,
        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$",
                message = "La contraseña debe tener al menos una letra y un número") String passwordNueva,
        @NotBlank(message = "Debes confirmar la contraseña nueva") String confirmarPassword) {

    @AssertTrue(message = "Las contraseñas no coinciden")
    public boolean isPasswordsCoinciden() {
        return passwordNueva == null || passwordNueva.equals(confirmarPassword);
    }

    @AssertTrue(message = "La contraseña nueva debe ser distinta de la actual")
    public boolean isDistintaDeLaActual() {
        return passwordNueva == null || !passwordNueva.equals(passwordActual);
    }
}
