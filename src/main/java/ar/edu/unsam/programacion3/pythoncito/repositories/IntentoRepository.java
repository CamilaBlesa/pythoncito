
package ar.edu.unsam.programacion3.pythoncito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import ar.edu.unsam.programacion3.pythoncito.models.Intento;

public interface IntentoRepository extends JpaRepository<Intento, Long> {

        Intento findTopByUsuarioIdAndEjercicioIdOrderByNumeroIntentoDesc(
                        Long usuarioId,
                        Long ejercicioId);

        List<Intento> findByUsuarioIdAndEjercicioIdOrderByNumeroIntentoAsc(
                        Long usuarioId,
                        Long ejercicioId);

        boolean existsByUsuarioIdAndEjercicioIdAndCorrectaTrue(
                        Long usuarioId,
                        Long ejercicioId);

        long countByUsuarioIdAndEjercicioIdAndCorrectaFalse(
                        Long usuarioId,
                        Long ejercicioId);
}