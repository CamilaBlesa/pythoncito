package ar.edu.unsam.programacion3.pythoncito.mappers;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.PistaDTO;

import ar.edu.unsam.programacion3.pythoncito.models.BloqueCodigo;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Pista;

import java.util.ArrayList;

public class EjercicioMapper {

    private EjercicioMapper() {
        // Constructor privado para evitar instanciación
    }

    public static EjercicioDTO aDTOAlumno(Ejercicio ejercicio) {

        ArrayList<BloqueCodigoDTO> bloquesDTO = new ArrayList<>();
        for (BloqueCodigo bloque : ejercicio.getBloques()) {
            bloquesDTO.add(new BloqueCodigoDTO(
                        bloque.getId(),
                        bloque.getContenido(),
                        bloque.getOrdenPresentacion()
            ));
        }

        ArrayList<PistaDTO> pistasDTO = new ArrayList<>();
        for (Pista pista : ejercicio.getPistas()) {
            pistasDTO.add(new PistaDTO(
                        pista.getId(),
                        pista.getTexto(),
                        pista.getOrden()
            ));
        }

        EjercicioDTO dto = new EjercicioDTO();
        dto.setId(ejercicio.getId());
        dto.setTitulo(ejercicio.getTitulo());
        dto.setConsigna(ejercicio.getConsigna());
        dto.setExplicacion(ejercicio.getExplicacion());
        dto.setEjemploDiferente(ejercicio.getEjemploDiferente());
        dto.setResultadoEjemplo(ejercicio.getResultadoEjemplo());
        dto.setBloques(bloquesDTO);
        dto.setPistas(pistasDTO);
        return dto;
    }

    
    public static EjercicioAdminDTO aDTOAdmin(Ejercicio ejercicio) {

        ArrayList<BloqueCodigoAdminDTO> bloquesDTO = new ArrayList<>();
        for (BloqueCodigo bloque : ejercicio.getBloques()) {
            bloquesDTO.add(new BloqueCodigoAdminDTO(
                        bloque.getId(),
                        bloque.getContenido(),
                        bloque.getOrdenPresentacion(),
                        bloque.getOrdenCorrecto()
            ));
        }

        ArrayList<PistaDTO> pistasDTO = new ArrayList<>();
        for (Pista pista : ejercicio.getPistas()) {
            pistasDTO.add(new PistaDTO(
                        pista.getId(),
                        pista.getTexto(),
                        pista.getOrden()
            ));
        }

        EjercicioAdminDTO dto = new EjercicioAdminDTO();
        dto.setId(ejercicio.getId());
        dto.setTitulo(ejercicio.getTitulo());
        dto.setConsigna(ejercicio.getConsigna());
        dto.setExplicacion(ejercicio.getExplicacion());
        dto.setEjemploDiferente(ejercicio.getEjemploDiferente());
        dto.setResultadoEjemplo(ejercicio.getResultadoEjemplo());
        dto.setCodigoEsperado(ejercicio.getCodigoEsperado());
        dto.setOrdenPresentacion(ejercicio.getOrdenPresentacion());
        dto.setNivelId(ejercicio.getNivel().getId());
        dto.setBloques(bloquesDTO);
        dto.setPistas(pistasDTO);
        return dto;
    }
}