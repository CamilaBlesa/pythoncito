package ar.edu.unsam.programacion3.pythoncito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.unsam.programacion3.pythoncito.model.Nivel;

public interface NivelRepository extends JpaRepository<Nivel, Long> {
}
