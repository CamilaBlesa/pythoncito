package ar.edu.unsam.programacion3.pythoncito.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(String.format("%s no encontrado con id: %d", recurso, id));
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}