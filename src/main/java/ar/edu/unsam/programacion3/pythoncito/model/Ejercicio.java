
package ar.edu.unsam.programacion3.pythoncito.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ejercicios")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representa un ejercicio dentro de un nivel.")

public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del ejercicio.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(length = 100, nullable = false)
    @Schema(
        description = "Título del ejercicio.",
        example = "Ejercicio 1: Mostrar texto en pantalla",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @Column(length = 500, nullable = false)
    @Schema(
        description = "Instrucciones del ejercicio.",
        example = "Escribe un programa que imprima 'Hola Mundo' en la pantalla.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String consigna;

    @Column(length = 1000, nullable = false)
    @Schema(
        description = "Código esperado del ejercicio.",
        example = "print('Hola Mundo')",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String codigoEsperado;

    @Column(nullable = false)
    @Schema(
        description = "Indica el orden de presentación del ejercicio.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer ordenPresentacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nivel_id", nullable = false)
    @JsonIgnoreProperties({"ejercicios"})
    @Schema(
        description = "Nivel al que pertenece el ejercicio."
    )
    private Nivel nivel;

    @OneToMany(mappedBy = "ejercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordenPresentacion ASC")
    @JsonIgnoreProperties({"ejercicio"})
    @Schema(
        description = "Bloques de código que se encuentran dentro del ejercicio."
    )
    private List<BloqueCodigo> bloques = new ArrayList<>();

    public Ejercicio() {
    }

    public Long getId() {
        return id;
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

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public List<BloqueCodigo> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloqueCodigo> bloques) {
        this.bloques = bloques;
    }

    @Override
    public String toString() {
        return "Ejercicio{" +
            "id=" + id +
            ", titulo='" + titulo + '\'' +
            ", consigna='" + consigna + '\'' +
            ", codigoEsperado='" + codigoEsperado + '\'' +
            ", ordenPresentacion=" + ordenPresentacion +
            '}';
    }
}
