package ar.edu.unsam.programacion3.pythoncito.mappers;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;

import java.util.List;

public class EjercicioMapper {

    private EjercicioMapper() {
        // Constructor privado para evitar instanciación
    }

    public static EjercicioDTO aDTOAlumno(Ejercicio ejercicio) {

        List<BloqueCodigoDTO> bloquesDTO = ejercicio.getBloques()
                .stream()
                .map(bloque -> new BloqueCodigoDTO(
                        bloque.getId(),
                        bloque.getContenido(),
                        bloque.getOrdenPresentacion()
                ))
                .toList();

        return new EjercicioDTO(
                ejercicio.getId(),
                ejercicio.getTitulo(),
                ejercicio.getConsigna(),
                bloquesDTO
        );
    }

    
    public static EjercicioAdminDTO aDTOAdmin(Ejercicio ejercicio) {

        List<BloqueCodigoAdminDTO> bloquesDTO = ejercicio.getBloques()
                .stream()
                .map(bloque -> new BloqueCodigoAdminDTO(
                        bloque.getId(),
                        bloque.getContenido(),
                        bloque.getOrdenPresentacion(),
                        bloque.getOrdenCorrecto()
                ))
                .toList();

        return new EjercicioAdminDTO(
                ejercicio.getId(),
                ejercicio.getTitulo(),
                ejercicio.getConsigna(),
                bloquesDTO
        );
    }
}