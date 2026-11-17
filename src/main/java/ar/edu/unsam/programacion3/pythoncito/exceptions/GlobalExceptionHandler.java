package ar.edu.unsam.programacion3.pythoncito.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> crearRespuesta(
            HttpStatus estado,
            String mensaje) {

        Map<String, Object> cuerpo = Map.of(
                "timestamp", Instant.now().toString(),
                "status", estado.value(),
                "error", estado.getReasonPhrase(),
                "mensaje", mensaje
        );

        return ResponseEntity.status(estado).body(cuerpo);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException excepcion) {

        return crearRespuesta(
                HttpStatus.NOT_FOUND,
                excepcion.getMessage());
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            ValidacionException excepcion) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                excepcion.getMessage());
    }

    @ExceptionHandler(CredencialInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(
            CredencialInvalidaException excepcion) {

        return crearRespuesta(
                HttpStatus.UNAUTHORIZED,
                excepcion.getMessage());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, Object>> manejarAccesoDenegado(
            AccesoDenegadoException excepcion) {

        return crearRespuesta(
                HttpStatus.FORBIDDEN,
                excepcion.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJSONInvalido(
            HttpMessageNotReadableException excepcion) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición está vacío o tiene un formato inválido.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentosInvalidos(
            MethodArgumentNotValidException excepcion) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen con las validaciones.");
    }
}