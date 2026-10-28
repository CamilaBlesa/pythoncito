package ar.edu.unsam.programacion3.pythoncito.dto;

import java.util.List;

public class EjercicioDTO {

    private Long id;
    private String titulo;
    private String consigna;
    private List<BloqueCodigoDTO> bloques;

    public EjercicioDTO() {
    }

    public EjercicioDTO(Long id, String titulo, String consigna,
                        List<BloqueCodigoDTO> bloques) {
        this.id = id;
        this.titulo = titulo;
        this.consigna = consigna;
        this.bloques = bloques;
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

    public List<BloqueCodigoDTO> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloqueCodigoDTO> bloques) {
        this.bloques = bloques;
    }
}