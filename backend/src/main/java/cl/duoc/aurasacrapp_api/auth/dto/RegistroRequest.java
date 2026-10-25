package cl.duoc.aurasacrapp_api.auth.dto;

import cl.duoc.aurasacrapp_api.usuario.Rol;
import jakarta.validation.constraints.*;

import java.util.Locale;

/** Datos que envía la app al crear una cuenta (CU-26). */
public record RegistroRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String apellido,

        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$", message = "El correo no tiene un formato válido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "La contraseña debe tener al menos una letra y un número")
        String password,

        @NotBlank(message = "Debes confirmar la contraseña")
        String confirmarPassword,

        @NotNull(message = "El tipo de cuenta es obligatorio")
        Rol rol,

        @Size(max = 120, message = "La institución no puede superar 120 caracteres")
        String institucion,

        @NotNull(message = "Debes aceptar la política de privacidad")
        @AssertTrue(message = "Debes aceptar la política de privacidad")
        Boolean aceptaPrivacidad) {

    // Se ejecuta al recibir el JSON, antes de validar: quita espacios y deja el correo en minúsculas.
    public RegistroRequest {
        nombre = limpiar(nombre);
        apellido = limpiar(apellido);
        correo = correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
        institucion = limpiar(institucion);
    }

    private static String limpiar(String texto) {
        return texto == null ? null : texto.trim();
    }

    @AssertTrue(message = "Las contraseñas no coinciden")
    public boolean isPasswordsCoinciden() {
        return password == null || password.equals(confirmarPassword);
    }

    @AssertTrue(message = "Solo puedes registrarte como VISITANTE o DOCENTE")
    public boolean isRolPermitido() {
        return rol == null || rol == Rol.VISITANTE || rol == Rol.DOCENTE;
    }

    @AssertTrue(message = "Si eres docente, indica tu colegio o institución (mínimo 2 caracteres)")
    public boolean isInstitucionValida() {
        return rol != Rol.DOCENTE || (institucion != null && institucion.length() >= 2);
    }
}
