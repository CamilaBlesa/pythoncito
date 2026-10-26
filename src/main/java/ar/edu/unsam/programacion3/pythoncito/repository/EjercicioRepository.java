package ar.edu.unsam.programacion3.pythoncito.repository;

import ar.edu.unsam.programacion3.pythoncito.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
}