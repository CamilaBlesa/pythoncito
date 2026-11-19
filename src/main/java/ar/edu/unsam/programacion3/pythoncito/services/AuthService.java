package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import ar.edu.unsam.programacion3.pythoncito.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;

import java.util.UUID;

/**
 * Servicio encargado de la autenticacion, registro y recuperacion de tokens de usuarios.
 */
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

    /**
     * Registra un nuevo estudiante anonimo asignando un UUID y retornando su token JWT.
     * @return token JWT generado para el estudiante
     */
    @Transactional
    public String crearEstudiante() {

        Usuario estudiante = new Usuario();
        estudiante.setUsername(UUID.randomUUID().toString());
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);

        usuarioRepository.save(estudiante);

        return jwtService.generarToken(estudiante);
    }

    /**
     * Autentica al administrador mediante clave secreta y retorna su token JWT.
     * @param clave clave enviada por el usuario
     * @return token JWT de administrador
     */
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

    /**
     * Renueva y recupera el token de sesion de un estudiante a partir de un token previo.
     * @param token token actual del estudiante
     * @return nuevo token JWT generado
     */
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
