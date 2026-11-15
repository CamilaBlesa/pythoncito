package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.mappers.EjercicioMapper;
import ar.edu.unsam.programacion3.pythoncito.model.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.repositories.EjercicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    public EjercicioService(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }

    public List<EjercicioDTO> listarParaAlumno() {
        return ejercicioRepository.findAll()
                .stream()
                .map(EjercicioMapper::aDTOAlumno)
                .toList();
    }

    public EjercicioDTO obtenerEjercicioParaAlumno(Long id) {
        for (Ejercicio ejercicio : ejercicioRepository.findAllById(List.of(id))) {
           EjercicioMapper.aDTOAlumno(ejercicio);
        }
        return null;
    }

    public List<EjercicioAdminDTO> listarParaAdmin() {
        return ejercicioRepository.findAll()
                .stream()
                .map(EjercicioMapper::aDTOAdmin)
                .toList();
    }

    public EjercicioAdminDTO obtenerEjercicioParaAdmin(Long id) {
        for (Ejercicio ejercicio : ejercicioRepository.findAllById(List.of(id))) {
            EjercicioMapper.aDTOAdmin(ejercicio);
        }
        return null;
    }
}
