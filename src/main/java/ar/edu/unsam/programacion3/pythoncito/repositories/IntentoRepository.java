package ar.edu.unsam.programacion3.pythoncito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unsam.programacion3.pythoncito.models.Intento;

public interface IntentoRepository extends JpaRepository<Intento, Long> {
}
