
package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import ar.edu.unsam.programacion3.pythoncito.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;

import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Value("${pythoncito.security.admin-token}")
    private String claveAdmin;

    public AuthService(
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public String crearAlumno() {

        Usuario alumno = new Usuario();
        alumno.setUsername(UUID.randomUUID().toString());
        alumno.setRol(Usuario.Rol.ALUMNO);

        usuarioRepository.save(alumno);

        return jwtService.generarToken(alumno);
    }

    public String autenticarAdmin(String clave) {

        if (!claveAdmin.equals(clave)) {
            throw new CredencialInvalidaException("Clave de administrador incorrecta");
        }

        Usuario admin = usuarioRepository.findByUsername("admin")
                .orElseGet(() -> {
                    Usuario nuevoAdmin = new Usuario();
                    nuevoAdmin.setUsername("admin");
                    nuevoAdmin.setRol(Usuario.Rol.ADMIN);
                    return usuarioRepository.save(nuevoAdmin);
                });

        return jwtService.generarToken(admin);
    }

    @Transactional(readOnly = true)
    public String recuperarAlumno(String token) {

        Long idUsuario = jwtService.obtenerIdUsuario(token);

        Usuario alumno = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new CredencialInvalidaException("Alumno no encontrado"));

        if (alumno.getRol() != Usuario.Rol.ALUMNO) {
            throw new CredencialInvalidaException(
                    "El token no pertenece a un alumno");
        }

        return jwtService.generarToken(alumno);
    }
}