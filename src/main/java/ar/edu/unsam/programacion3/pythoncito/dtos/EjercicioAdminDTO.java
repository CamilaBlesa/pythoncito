
package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

@Schema(description = "Ejercicio con la informacion necesaria para el panel administrativo.")
public class EjercicioAdminDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String titulo;

    private String consigna;

    private String explicacion;

    private String ejemploDiferente;

    private String resultadoEjemplo;

    private String codigoEsperado;

    private Integer ordenPresentacion;

    private Long nivelId;

    private ArrayList<BloqueCodigoAdminDTO> bloques;

    private ArrayList<PistaDTO> pistas;

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

    public String getCodigoEsperado() {
        return codigoEsperado;
    }

    public void setCodigoEsperado(String codigoEsperado) {
        this.codigoEsperado = codigoEsperado;
    }

    public Integer getOrdenPresentacion() {
        return ordenPresentacion;
    }

    public void setOrdenPresentacion(Integer ordenPresentacion) {
        this.ordenPresentacion = ordenPresentacion;
    }

    public Long getNivelId() {
        return nivelId;
    }

    public void setNivelId(Long nivelId) {
        this.nivelId = nivelId;
    }

    public ArrayList<BloqueCodigoAdminDTO> getBloques() {
        return bloques;
    }

    public void setBloques(ArrayList<BloqueCodigoAdminDTO> bloques) {
        this.bloques = bloques;
    }

    public ArrayList<PistaDTO> getPistas() {
        return pistas;
    }

    public void setPistas(ArrayList<PistaDTO> pistas) {
        this.pistas = pistas;
    }
}
