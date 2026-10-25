package ar.edu.unsam.programacion3.pythoncito.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;


@Entity
@Table(name = "Niveles")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        description = "Representa un nivel dentro de la ruta de aprendizaje del usuario."
)
public class Nivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del nivel.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(nullable = false)
    @Schema(
            description = "Nombre del nivel.",
            example = "Nivel 1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @Column(nullable = false)
    @Schema(
            description = "Tema del nivel.",
            example = "Imprimir texto en consola",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tema;

    @Column(nullable = false)
    @Schema(
            description = "Estado de activación del nivel.",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean activo;

    @Column(nullable = false)
    @Schema(
        description = "Orden de presentación en la interfaz visual.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer orden;

    @OneToMany (mappedBy = "nivel", cascade = CascadeType.ALL)
    @OrderBy("Orden ASC")
    @Schema(
            description = "Lista de ejercicios del nivel.",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<Ejercicio> ejercicios = new ArrayList<>();

    public Nivel() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @JsonProperty("name")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTema() {
        return tema;
    }

    @JsonProperty("topic")
    public void setTema(String tema) {
        this.tema = tema;
    }

    public boolean isActivo() {
        return activo;
    }

    @JsonProperty ("active")
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
                ", nombre='" + nombre + '\'' +
                ", tema='" + tema + '\'' +
                ", activo=" + activo +
                ", orden=" + orden +
                ", ejercicios=" + ejercicios +
                '}';
    }
}