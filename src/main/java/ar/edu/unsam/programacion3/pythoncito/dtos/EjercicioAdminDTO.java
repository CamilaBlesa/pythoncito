
package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ejercicio con la informacion necesaria para el panel administrativo.")
public class EjercicioAdminDTO {

    @Schema(
        description = "Identificador unico del ejercicio.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Titulo del ejercicio.",
        example = "Tu primer mensaje",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @Schema(
        description = "Consigna del ejercicio.",
        example = "Ordena los bloques para que Python muestre un saludo.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String consigna;

    @Schema(
        description = "Bloques disponibles y orden correcto de la solucion.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<BloqueCodigoAdminDTO> bloques;

    public EjercicioAdminDTO() {
    }

    public EjercicioAdminDTO(Long id, String titulo,
            String consigna, List<BloqueCodigoAdminDTO> bloques) {
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

    public List<BloqueCodigoAdminDTO> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloqueCodigoAdminDTO> bloques) {
        this.bloques = bloques;
    }
}
