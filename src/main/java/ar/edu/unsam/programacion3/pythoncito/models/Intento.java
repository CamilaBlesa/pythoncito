package ar.edu.unsam.programacion3.pythoncito.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "intentos")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Intento de resolucion de un ejercicio.")
public class Intento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador unico del intento.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    @JsonIgnoreProperties({"intentos", "bloques", "pistas", "nivel"})
    @Schema(
        description = "Ejercicio resuelto en el intento."
    )
    private Ejercicio ejercicio;

    @Column(nullable = false)
    @Schema(
        description = "Numero correlativo del intento para el ejercicio.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer numeroIntento;

    @Column(nullable = false)
    @Schema(
        description = "Indica si la respuesta fue correcta.",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean correcta;

    @Column(nullable = false)
    @Schema(
        description = "Fecha y hora en que se realizo el intento.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime fechaHora;

    public Intento() {
    }

    public Long getId() {
        return id;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public Integer getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(Integer numeroIntento) {
        this.numeroIntento = numeroIntento;
    }

    public Boolean getCorrecta() {
        return correcta;
    }

    public void setCorrecta(Boolean correcta) {
        this.correcta = correcta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
