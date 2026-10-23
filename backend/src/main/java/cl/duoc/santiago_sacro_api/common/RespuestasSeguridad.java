package cl.duoc.santiago_sacro_api.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RespuestasSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    // 401: no hay token, o es inválido o está vencido.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        escribir(request, response, HttpStatus.UNAUTHORIZED, "No autenticado",
                "Debes iniciar sesión: el token falta, es inválido o está vencido.");
    }

    // 403: hay token, pero el rol no tiene permiso.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escribir(request, response, HttpStatus.FORBIDDEN, "Acceso denegado",
                "Tu rol no tiene permiso para esta acción.");
    }

    private void escribir(HttpServletRequest request, HttpServletResponse response,
                          HttpStatus estado, String titulo, String detalle) throws IOException {
        response.setStatus(estado.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"type":"about:blank","title":"%s","status":%d,"detail":"%s","instance":"%s"}"""
                .formatted(titulo, estado.value(), detalle, request.getRequestURI().replace("\"", "")));
    }
}
