package ar.edu.unsam.programacion3.pythoncito.exceptions;

public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}