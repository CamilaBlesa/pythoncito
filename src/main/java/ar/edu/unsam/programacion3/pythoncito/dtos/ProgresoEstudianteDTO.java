
package ar.edu.unsam.programacion3.pythoncito.dtos;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Progreso de un estudiante en un nivel.")
public class ProgresoEstudianteDTO {

    private Long nivelId;
    private String titulo;
    private String tema;
    private LocalDateTime fechaCompletado;

    public ProgresoEstudianteDTO() {
    }

    public ProgresoEstudianteDTO(
            Long nivelId,
            String titulo,
            String tema,
            LocalDateTime fechaCompletado) {

        this.nivelId = nivelId;
        this.titulo = titulo;
        this.tema = tema;
        this.fechaCompletado = fechaCompletado;
    }

    public Long getNivelId() {
        return nivelId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTema() {
        return tema;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }
}