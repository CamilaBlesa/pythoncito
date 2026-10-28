package ar.edu.unsam.programacion3.pythoncito.dto;

public class BloqueCodigoDTO {

    private Long id;
    private String contenido;
    private Integer ordenPresentacion;

    public BloqueCodigoDTO() {
    }

    public BloqueCodigoDTO(Long id, String contenido, Integer ordenPresentacion) {
        this.id = id;
        this.contenido = contenido;
        this.ordenPresentacion = ordenPresentacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Integer getOrdenPresentacion() {
        return ordenPresentacion;
    }

    public void setOrdenPresentacion(Integer ordenPresentacion) {
        this.ordenPresentacion = ordenPresentacion;
    }
}