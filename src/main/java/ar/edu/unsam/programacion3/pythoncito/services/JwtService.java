package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.models.Usuario;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Servicio encargado de la emision, firma y decodificacion de tokens JWT mediante Spring Security.
 */
@Service
public class JwtService {

    private static final long DURACION_ESTUDIANTE = 30L * 24 * 60 * 60;
    private static final long DURACION_ADMIN = 2L * 60 * 60;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Genera un token JWT firmado para el usuario con duracion y roles correspondientes.
     * @param usuario usuario para el cual se emitira el token
     * @return valor string del token JWT firmado
     */
    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        long duracion = obtenerDuracionToken(usuario);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("pythoncito")
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(duracion))
                .subject(usuario.getId().toString())
                .claim("roles", usuario.getRol().name())
                .build();

        JwsHeader encabezado = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(encabezado, claims)
        ).getTokenValue();
    }

    private long obtenerDuracionToken(Usuario usuario) {

        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            return DURACION_ADMIN;
        }

        return DURACION_ESTUDIANTE;
    }

    /**
     * Extrae el identificador de usuario almacenado en el claim subject del token JWT.
     * @param token token JWT en formato string
     * @return identificador unico del usuario
     */
    public Long obtenerIdUsuario(String token) {
        return Long.parseLong(jwtDecoder.decode(token).getSubject());
    }
}
