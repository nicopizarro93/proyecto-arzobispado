package cl.duoc.santiago_sacro_api.common;

import cl.duoc.santiago_sacro_api.auth.CorreoYaRegistradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convierte las excepciones en respuestas JSON con el formato estándar ProblemDetail (RFC 9457).
 * Los errores comunes de Spring (404, JSON mal formado, parámetro inválido) los resuelve la clase padre.
 */
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    // 400: datos que no cumplen las validaciones de @Valid, con el detalle por campo.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Hay datos inválidos en la solicitud.");
        problema.setTitle("Datos inválidos");
        problema.setProperty("errores", errores);
        return ResponseEntity.badRequest().body(problema);
    }

    // 409: el correo ya existe.
    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ProblemDetail correoRepetido(CorreoYaRegistradoException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("Correo ya registrado");
        return problema;
    }

    // 409: una regla de la base (por ejemplo, dos registros simultáneos con el mismo correo).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail conflictoDeDatos(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Los datos entran en conflicto con un registro existente.");
        problema.setTitle("Conflicto de datos");
        return problema;
    }

    // 500: cualquier error no previsto. El detalle va al log, nunca al cliente.
    @ExceptionHandler(Exception.class)
    public ProblemDetail errorInesperado(Exception ex) {
        log.error("Error no controlado", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intenta de nuevo más tarde.");
        problema.setTitle("Error interno");
        return problema;
    }
}