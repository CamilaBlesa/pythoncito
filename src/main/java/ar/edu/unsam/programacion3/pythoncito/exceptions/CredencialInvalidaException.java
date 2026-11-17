package ar.edu.unsam.programacion3.pythoncito.exceptions;

public class CredencialInvalidaException extends RuntimeException {

    public CredencialInvalidaException(String mensaje) {
        super(mensaje);
    }
}