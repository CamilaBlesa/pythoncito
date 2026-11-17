
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
    public String crearEstudiante() {

        Usuario estudiante = new Usuario();
        estudiante.setUsername(UUID.randomUUID().toString());
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);

        usuarioRepository.save(estudiante);

        return jwtService.generarToken(estudiante);
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
    public String recuperarEstudiante(String token) {

        Long idUsuario = jwtService.obtenerIdUsuario(token);

        Usuario estudiante = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new CredencialInvalidaException("Estudiante no encontrado"));

        if (estudiante.getRol() != Usuario.Rol.ESTUDIANTE) {
            throw new CredencialInvalidaException(
                    "El token no pertenece a un estudiante");
        }

        return jwtService.generarToken(estudiante);
    }
}