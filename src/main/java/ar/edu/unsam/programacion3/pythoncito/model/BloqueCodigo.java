package ar.edu.unsam.programacion3.pythoncito.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "bloques_codigo")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representa un bloque de código dentro de un ejercicio.")
public class BloqueCodigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del bloque de código.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(length = 100, nullable = false)
    @Schema(
        description = "Contenido de código que contiene el bloque.",
        example = "print(",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String contenido;

    @Column(nullable = false)
    @Schema(
        description = "Indica el orden de presentación del bloque en el ejercicio.",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer ordenPresentacion;

    @Column(nullable = false)
    @Schema(
        description = "Indica el orden correcto del bloque en la solución de un ejercicio.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
        )
        private Integer ordenCorrecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    @JsonIgnoreProperties({"bloques", "nivel"})
    @Schema(
        description = "Ejercicio al que pertenece el bloque de código."
    )
    private Ejercicio ejercicio;

    public BloqueCodigo() {
    }

    public Long getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    @JsonProperty("code")
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Integer getOrdenPresentacion() {
        return ordenPresentacion;
    }

    @JsonProperty("order")
    public void setOrdenPresentacion(Integer ordenPresentacion) {
        this.ordenPresentacion = ordenPresentacion;
    }

    public Integer getOrdenCorrecto() {
        return ordenCorrecto;
    }

    @JsonProperty("correctOrder")
    public void setOrdenCorrecto(Integer ordenCorrecto) {
        this.ordenCorrecto = ordenCorrecto;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    @JsonProperty("exercise")
    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    @Override
    public String toString() {
        return "BloqueCodigo{" +
            "id=" + id +
            ", contenido='" + contenido + '\'' +
            ", ordenPresentacion=" + ordenPresentacion +
            ", ordenCorrecto=" + ordenCorrecto +
            '}';
    }
    
}
