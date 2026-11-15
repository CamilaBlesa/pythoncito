package ar.edu.unsam.programacion3.pythoncito.mappers;

import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.model.Nivel;

public final class NivelMapper {

    private NivelMapper() {
    }

    public static NivelDTO aDTO(Nivel nivel) {
        return new NivelDTO(
                nivel.getId(),
                nivel.getTitulo(),
                nivel.getTema(),
                nivel.getActivo(),
                nivel.getOrdenPresentacion()
        );
    }
}
