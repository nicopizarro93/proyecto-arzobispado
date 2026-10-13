package cl.duoc.santiago_sacro_api.auth;

public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException(String correo) {
        super("El correo " + correo + " ya está registrado. Puedes iniciar sesión.");
    }
}
