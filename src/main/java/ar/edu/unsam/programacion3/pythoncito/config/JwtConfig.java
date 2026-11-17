
package ar.edu.unsam.programacion3.pythoncito.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${pythoncito.security.jwt.secret}")
    private String secret;

    private SecretKey obtenerClave() {
        byte[] clave = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(clave, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {

        OctetSequenceKey jwk = new OctetSequenceKey.Builder(obtenerClave())
                .algorithm(JWSAlgorithm.HS256)
                .keyID("pythoncito-key")
                .build();

        return new NimbusJwtEncoder(
                new ImmutableJWKSet<SecurityContext>(new JWKSet(jwk))
        );
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(obtenerClave())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}