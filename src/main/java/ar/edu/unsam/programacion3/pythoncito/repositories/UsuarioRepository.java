
package ar.edu.unsam.programacion3.pythoncito.repositories;

import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

}