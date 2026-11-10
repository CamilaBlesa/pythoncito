
package ar.edu.unsam.programacion3.pythoncito.dto;

public class BloqueCodigoAdminDTO {

    private Long id;
    private String contenido;
    private Integer ordenPresentacion;
    private Integer ordenCorrecto;

    public BloqueCodigoAdminDTO() {
    }

    public BloqueCodigoAdminDTO(
            Long id,
            String contenido,
            Integer ordenPresentacion,
            Integer ordenCorrecto) {
        this.id = id;
        this.contenido = contenido;
        this.ordenPresentacion = ordenPresentacion;
        this.ordenCorrecto = ordenCorrecto;
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

    public Integer getOrdenCorrecto() {
        return ordenCorrecto;
    }

    public void setOrdenCorrecto(Integer ordenCorrecto) {
        this.ordenCorrecto = ordenCorrecto;
    }
}
