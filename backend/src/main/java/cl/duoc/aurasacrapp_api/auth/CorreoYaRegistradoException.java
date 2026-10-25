package cl.duoc.aurasacrapp_api.auth;

public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException(String correo) {
        super("El correo " + correo + " ya está registrado. Puedes iniciar sesión.");
    }
}
