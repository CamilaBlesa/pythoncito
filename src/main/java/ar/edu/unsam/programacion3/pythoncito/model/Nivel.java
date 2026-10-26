package ar.edu.unsam.programacion3.pythoncito.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "niveles")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representa un nivel dentro de la ruta de aprendizaje del usuario.")
public class Nivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del nivel.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(length = 100, nullable = false)
    @Schema(
        description = "Título del nivel.",
        example = "Nivel 1: Introducción a Python",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @Column(length = 100, nullable = false)
    @Schema(
        description = "Tema del nivel.",
        example = "Programación en Python",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tema;

    @Column(nullable = false)
    @Schema(
        description = "Indica si el nivel está activo.",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo;

    @Column(nullable = false)
    @Schema(description = "Orden de presentación del nivel.")
    private Integer ordenPresentacion;

    @OneToMany(mappedBy = "nivel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordenPresentacion ASC")
    @Schema(description = "Ejercicios que pertenecen al nivel.")
    private List<Ejercicio> ejercicios = new ArrayList<>();

    public Nivel() {
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

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    @Override
    public String toString() {
        return "Nivel{" +
            "id=" + id +
            ", titulo='" + titulo + '\'' +
            ", tema='" + tema + '\'' +
            ", activo=" + activo +
            ", ordenPresentacion=" + ordenPresentacion +
            '}';
    }
}