package cl.duoc.santiago_sacro_api.auth;

import cl.duoc.santiago_sacro_api.usuario.Rol;
import cl.duoc.santiago_sacro_api.usuario.Usuario;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    @Test
    void generaUnTokenFirmadoConElIdYElRol() {
        SecretKey clave = new SecretKeySpec(
                "clave-solo-para-pruebas-con-mas-de-32-caracteres".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        TokenService servicio = new TokenService(new NimbusJwtEncoder(new ImmutableSecret<>(clave)), 60);
        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();

        Usuario u = new Usuario();
        u.setId(7L);
        u.setCorreo("marta.diaz@ejemplo.cl");
        u.setRol(Rol.DOCENTE);

        TokenGenerado token = servicio.generar(u);
        Jwt jwt = decoder.decode(token.valor());

        assertEquals("7", jwt.getSubject());
        assertEquals("DOCENTE", jwt.getClaimAsString("rol"));
        assertEquals("marta.diaz@ejemplo.cl", jwt.getClaimAsString("correo"));
        assertTrue(jwt.getExpiresAt().isAfter(Instant.now()));
    }
}
