package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generarToken_estudiante_generaTokenConExito() {
        Usuario estudiante = new Usuario();
        ReflectionTestUtils.setField(estudiante, "id", 15L);
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);

        Jwt mockJwt = new Jwt(
                "token_estudiante_valor",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "15")
        );

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String token = jwtService.generarToken(estudiante);

        assertEquals("token_estudiante_valor", token);
    }

    @Test
    void generarToken_admin_generaTokenConExito() {
        Usuario admin = new Usuario();
        ReflectionTestUtils.setField(admin, "id", 1L);
        admin.setRol(Usuario.Rol.ADMIN);

        Jwt mockJwt = new Jwt(
                "token_admin_valor",
                Instant.now(),
                Instant.now().plusSeconds(7200),
                Map.of("alg", "HS256"),
                Map.of("sub", "1")
        );

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String token = jwtService.generarToken(admin);

        assertEquals("token_admin_valor", token);
    }

    @Test
    void obtenerIdUsuario_tokenValido_retornaId() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn("42");
        when(jwtDecoder.decode("token_test")).thenReturn(mockJwt);

        Long id = jwtService.obtenerIdUsuario("token_test");

        assertEquals(42L, id);
    }
}
