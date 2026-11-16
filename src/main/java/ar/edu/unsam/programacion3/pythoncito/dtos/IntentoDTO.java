package ar.edu.unsam.programacion3.pythoncito.dtos;

import java.time.LocalDateTime;

public class IntentoDTO {

    private Long id;
    private Long ejercicioId;
    private Integer numeroIntento;
    private Boolean correcta;
    private LocalDateTime fechaHora;

    public IntentoDTO() {
    }

    public IntentoDTO(Long id, Long ejercicioId, Integer numeroIntento,
                      Boolean correcta, LocalDateTime fechaHora) {
        this.id = id;
        this.ejercicioId = ejercicioId;
        this.numeroIntento = numeroIntento;
        this.correcta = correcta;
        this.fechaHora = fechaHora;
    }

    public Long getId() {
        return id;
    }

    public Long getEjercicioId() {
        return ejercicioId;
    }

    public Integer getNumeroIntento() {
        return numeroIntento;
    }

    public Boolean getCorrecta() {
        return correcta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
