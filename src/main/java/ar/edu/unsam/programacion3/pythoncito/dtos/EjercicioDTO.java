package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "Ejercicio que se presenta al estudiante sin exponer la solucion.")
public class EjercicioDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String titulo;

    private String consigna;

    private String explicacion;

    private String ejemploDiferente;

    private String resultadoEjemplo;

    private ArrayList<BloqueCodigoDTO> bloques;

    public EjercicioDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConsigna() {
        return consigna;
    }

    public void setConsigna(String consigna) {
        this.consigna = consigna;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public String getEjemploDiferente() {
        return ejemploDiferente;
    }

    public void setEjemploDiferente(String ejemploDiferente) {
        this.ejemploDiferente = ejemploDiferente;
    }

    public String getResultadoEjemplo() {
        return resultadoEjemplo;
    }

    public void setResultadoEjemplo(String resultadoEjemplo) {
        this.resultadoEjemplo = resultadoEjemplo;
    }

    public ArrayList<BloqueCodigoDTO> getBloques() {
        return bloques;
    }

    public void setBloques(ArrayList<BloqueCodigoDTO> bloques) {
        this.bloques = bloques;
    }
}