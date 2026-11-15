package ar.edu.unsam.programacion3.pythoncito.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "pistas")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Pista progresiva asociada a un ejercicio.")
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador unico de la pista.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(length = 1000, nullable = false)
    @Schema(
        description = "Texto de la pista.",
        example = "Observa primero que bloque debe ejecutarse.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String texto;

    @Column(nullable = false)
    @Schema(
        description = "Orden en que se entrega la pista.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    @JsonIgnoreProperties({"pistas", "bloques", "nivel"})
    private Ejercicio ejercicio;

    public Pista() {
    }

    public Long getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }
}
