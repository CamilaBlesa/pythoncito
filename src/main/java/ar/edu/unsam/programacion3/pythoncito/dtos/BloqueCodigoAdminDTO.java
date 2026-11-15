
package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bloque de codigo utilizado por el panel administrativo.")
public class BloqueCodigoAdminDTO {

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

    @Schema(
        description = "Orden correcto del bloque en la solucion.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer ordenCorrecto;

    public BloqueCodigoAdminDTO() {
    }

    public BloqueCodigoAdminDTO(Long id, String contenido,
            Integer ordenPresentacion, Integer ordenCorrecto) {
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
