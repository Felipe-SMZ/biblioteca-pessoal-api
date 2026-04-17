package com.felipesmz.bibliotecapessoal.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.felipesmz.bibliotecapessoal.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.issuer}")
    private String issuer;

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        // Tempo de expiração configurado em jwt.expiration-ms.
        Instant expiracao = agora.plusMillis(expirationMs);

        // Subject identifica o usuario por email; userId facilita correlacao no backend.
        return JWT.create()
                .withSubject(usuario.getEmail())
                .withClaim("userId", usuario.getId())
                .withIssuer(issuer)
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(expiracao))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret));
    }

    public DecodedJWT validarToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public Long getUserIdFromToken(String token) {
        return validarToken(token).getClaim("userId").asLong();
    }
}
