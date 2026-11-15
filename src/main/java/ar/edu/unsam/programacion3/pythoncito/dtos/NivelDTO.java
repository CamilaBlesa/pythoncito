package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un nivel de aprendizaje.")
public class NivelDTO {

    @Schema(
        description = "Identificador unico del nivel.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Titulo del nivel.",
        example = "Mensajes con print",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @Schema(
        description = "Tema principal del nivel.",
        example = "Mostrar texto con print",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tema;

    @Schema(
        description = "Indica si el nivel esta disponible.",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo;

    @Schema(
        description = "Orden de presentacion del nivel.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer ordenPresentacion;

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
}