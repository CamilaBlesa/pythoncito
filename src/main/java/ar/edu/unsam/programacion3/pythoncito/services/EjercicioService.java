package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;

public interface EjercicioService {

    EjercicioDTO obtenerEjercicioParaAlumno(Long id);

    EjercicioAdminDTO obtenerEjercicioParaAdmin(Long id);
}