package cl.duoc.aurasacrapp_api.auth;

public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException() {
        super("Demasiados intentos fallidos. Tu cuenta está bloqueada por 1 minuto.");
    }
}
