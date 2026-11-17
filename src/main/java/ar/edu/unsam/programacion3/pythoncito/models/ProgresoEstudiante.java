
package ar.edu.unsam.programacion3.pythoncito.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "progresos_estudiantes",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"usuario_id", "nivel_id"}
        )
    }
)
@Schema(description = "Registra los niveles completados por cada estudiante")
public class ProgresoEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del progreso.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @Schema(description = "Estudiante al que pertenece el progreso")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nivel_id", nullable = false)
    @Schema(description = "Nivel que completó el estudiante")
    private Nivel nivel;

    @Column(nullable = false)
    @Schema(
        description = "Fecha y hora en que se completó el nivel.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime fechaCompletado;

    public ProgresoEstudiante() {
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }
}