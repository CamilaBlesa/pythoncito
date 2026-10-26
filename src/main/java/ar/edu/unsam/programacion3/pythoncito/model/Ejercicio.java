
package ar.edu.unsam.programacion3.pythoncito.model;

public class Ejercicio {

    private String titulo;
    private String consigna;
    private String codigoEsperado;

    public Ejercicio(String titulo, String consigna, String codigoEsperado) {
        this.titulo = titulo;
        this.consigna = consigna;
        this.codigoEsperado = codigoEsperado;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConsigna() {
        return consigna;
    }

    public String getCodigoEsperado() {
        return codigoEsperado;
    }
}
