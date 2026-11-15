package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bloque de codigo visible para el alumno.")
public class BloqueCodigoDTO {

    @Schema(
        description = "Identificador unico del bloque.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Contenido del bloque.",
        example = "print(",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String contenido;

    @Schema(
        description = "Orden en que se muestra el bloque.",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    
    private Integer ordenPresentacion;

    public BloqueCodigoDTO() {
    }

    public BloqueCodigoDTO(Long id, String contenido, 
        Integer ordenPresentacion) {
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