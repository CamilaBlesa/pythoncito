package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Datos para la validacion de una respuesta enviada por el alumno.")
public class ValidacionPeticionDTO {

    private Long ejercicioId;
    private List<Long> bloqueIds;

    public ValidacionPeticionDTO() {
    }

    public ValidacionPeticionDTO(Long ejercicioId, List<Long> bloqueIds) {
        this.ejercicioId = ejercicioId;
        this.bloqueIds = bloqueIds;
    }

    public Long getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(Long ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public List<Long> getBloqueIds() {
        return bloqueIds;
    }

    public void setBloqueIds(List<Long> bloqueIds) {
        this.bloqueIds = bloqueIds;
    }
}
