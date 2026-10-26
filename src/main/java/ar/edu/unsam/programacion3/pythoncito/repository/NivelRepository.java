package ar.edu.unsam.programacion3.pythoncito.repository;

import ar.edu.unsam.programacion3.pythoncito.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NivelRepository extends JpaRepository<Nivel, Long> {
}