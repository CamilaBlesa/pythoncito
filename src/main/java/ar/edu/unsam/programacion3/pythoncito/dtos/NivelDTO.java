package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un nivel de aprendizaje.")
public class NivelDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String titulo;

    private String tema;

    private Boolean activo;

    private Integer ordenPresentacion;

    private boolean completado;
    private boolean desbloqueado;

    public NivelDTO() {
    }

    public NivelDTO(Long id, String titulo, String tema,
            Boolean activo, Integer ordenPresentacion) {
        this.id = id;
        this.titulo = titulo;
        this.tema = tema;
        this.activo = activo;
        this.ordenPresentacion = ordenPresentacion;
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

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getOrdenPresentacion() {
        return ordenPresentacion;
    }

    public void setOrdenPresentacion(Integer ordenPresentacion) {
        this.ordenPresentacion = ordenPresentacion;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}