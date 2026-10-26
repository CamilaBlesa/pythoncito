package ar.edu.unsam.programacion3.pythoncito.repository;

import ar.edu.unsam.programacion3.pythoncito.model.BloqueCodigo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueCodigoRepository extends JpaRepository<BloqueCodigo, Long> {
}