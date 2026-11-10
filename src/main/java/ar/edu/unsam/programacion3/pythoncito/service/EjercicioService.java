package ar.edu.unsam.programacion3.pythoncito.service;

import ar.edu.unsam.programacion3.pythoncito.dto.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.dto.EjercicioAdminDTO;

public interface EjercicioService {

    EjercicioDTO obtenerEjercicioParaAlumno(Long id);

    EjercicioAdminDTO obtenerEjercicioParaAdmin(Long id);
}