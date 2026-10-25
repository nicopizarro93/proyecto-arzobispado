package cl.duoc.aurasacrapp_api.auth.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Locale;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") String correo,
        @NotBlank(message = "La contraseña es obligatoria") String password) {

    public LoginRequest {
        correo = correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }
}
