package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;
import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import ar.edu.unsam.programacion3.pythoncito.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "claveAdmin", "secret_admin_key");
    }

    @Test
    void crearEstudiante_guardaUsuarioYGeneraToken() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token_estudiante_mock");

        String token = authService.crearEstudiante();

        assertEquals("token_estudiante_mock", token);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(jwtService).generarToken(any(Usuario.class));
    }

    @Test
    void autenticarAdmin_claveCorrecta_retornaToken() {
        Usuario admin = new Usuario();
        ReflectionTestUtils.setField(admin, "id", 1L);
        admin.setUsername("admin");
        admin.setRol(Usuario.Rol.ADMIN);

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(jwtService.generarToken(admin)).thenReturn("token_admin_mock");

        String token = authService.autenticarAdmin("secret_admin_key");

        assertEquals("token_admin_mock", token);
    }

    @Test
    void autenticarAdmin_adminNoExistePreviamente_creaAdminYRetornaToken() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token_admin_creado");

        String token = authService.autenticarAdmin("secret_admin_key");

        assertEquals("token_admin_creado", token);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void autenticarAdmin_claveIncorrecta_lanzaCredencialInvalidaException() {
        assertThrows(CredencialInvalidaException.class, () ->
                authService.autenticarAdmin("clave_invalida"));
    }

    @Test
    void recuperarEstudiante_tokenValido_retornaNuevoToken() {
        Usuario estudiante = new Usuario();
        ReflectionTestUtils.setField(estudiante, "id", 5L);
        estudiante.setUsername("alumno1");
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);

        when(jwtService.obtenerIdUsuario("token_antiguo")).thenReturn(5L);
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(estudiante));
        when(jwtService.generarToken(estudiante)).thenReturn("nuevo_token");

        String resultado = authService.recuperarEstudiante("token_antiguo");

        assertEquals("nuevo_token", resultado);
    }

    @Test
    void recuperarEstudiante_usuarioNoExiste_lanzaExcepcion() {
        when(jwtService.obtenerIdUsuario("token_invalido")).thenReturn(99L);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CredencialInvalidaException.class, () ->
                authService.recuperarEstudiante("token_invalido"));
    }

    @Test
    void recuperarEstudiante_rolNoEsEstudiante_lanzaExcepcion() {
        Usuario admin = new Usuario();
        ReflectionTestUtils.setField(admin, "id", 1L);
        admin.setRol(Usuario.Rol.ADMIN);

        when(jwtService.obtenerIdUsuario("token_admin")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(CredencialInvalidaException.class, () ->
                authService.recuperarEstudiante("token_admin"));
    }
}
