package ar.edu.unsam.programacion3.pythoncito.mapper;

import ar.edu.unsam.programacion3.pythoncito.model.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.dto.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.dto.BloqueCodigoDTO;
import ar.edu.unsam.programacion3.pythoncito.dto.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dto.BloqueCodigoAdminDTO;

import java.util.List;
import java.util.stream.Collectors;

public class EjercicioMapper {

    
    public static EjercicioDTO aDTOAlumno(Ejercicio ejercicio) {

        List<BloqueCodigoDTO> bloquesDTO = ejercicio.getBloques()
                .stream()
                .map(bloque -> new BloqueCodigoDTO(
                        bloque.getId(),
                        bloque.getContenido(),
                        bloque.getOrdenPresentacion()
                ))
                .collect(Collectors.toList());

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
                .collect(Collectors.toList());

        return new EjercicioAdminDTO(
                ejercicio.getId(),
                ejercicio.getTitulo(),
                ejercicio.getConsigna(),
                bloquesDTO
        );
    }
}