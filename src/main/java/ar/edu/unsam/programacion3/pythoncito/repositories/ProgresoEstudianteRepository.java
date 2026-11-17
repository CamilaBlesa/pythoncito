
package ar.edu.unsam.programacion3.pythoncito.repositories;

import ar.edu.unsam.programacion3.pythoncito.models.ProgresoEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgresoEstudianteRepository
        extends JpaRepository<ProgresoEstudiante, Long> {

    Optional<ProgresoEstudiante> findByUsuarioIdAndNivelId(
            Long usuarioId,
            Long nivelId
    );

    List<ProgresoEstudiante> findByUsuarioId(
            Long usuarioId
    );

    boolean existsByUsuarioIdAndNivelId(
            Long usuarioId,
            Long nivelId
    );
}