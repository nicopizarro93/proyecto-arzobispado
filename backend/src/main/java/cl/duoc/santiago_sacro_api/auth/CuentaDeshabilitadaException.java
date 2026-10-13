package cl.duoc.santiago_sacro_api.auth;

public class CuentaDeshabilitadaException extends RuntimeException {
    public CuentaDeshabilitadaException() {
        super("Tu cuenta está deshabilitada. Contacta al administrador de la red.");
    }
}
