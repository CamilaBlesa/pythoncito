package ar.edu.unsam.programacion3.pythoncito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import ar.edu.unsam.programacion3.pythoncito.models.Usuario;

@ExtendWith(MockitoExtension.class)
class JwtServiceClaimsTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtService jwtService;

    private JwtClaimsSet generarYCapturarClaims(Long id, Usuario.Rol rol) {
        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", id);
        usuario.setRol(rol);

        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60),
                Map.of("alg", "HS256"), Map.of("sub", id.toString()));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        jwtService.generarToken(usuario);

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        return captor.getValue().getClaims();
    }

    @Test
    void tokenEstudiante_duraTreintaDias_yLlevaRolSubjectEIssuer() {
        JwtClaimsSet claims = generarYCapturarClaims(15L, Usuario.Rol.ESTUDIANTE);

        assertEquals(Duration.ofDays(30),
                Duration.between(claims.getIssuedAt(), claims.getExpiresAt()));
        assertEquals("ESTUDIANTE", claims.getClaimAsString("roles"));
        assertEquals("15", claims.getSubject());
        assertEquals("pythoncito", claims.getClaims().get("iss"));
    }

    @Test
    void tokenAdmin_duraDosHoras_yLlevaRolAdmin() {
        JwtClaimsSet claims = generarYCapturarClaims(1L, Usuario.Rol.ADMIN);

        assertEquals(Duration.ofHours(2),
                Duration.between(claims.getIssuedAt(), claims.getExpiresAt()));
        assertEquals("ADMIN", claims.getClaimAsString("roles"));
        assertEquals("1", claims.getSubject());
    }
}