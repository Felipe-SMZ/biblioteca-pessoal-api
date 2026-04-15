package com.felipesmz.bibliotecapessoal.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.felipesmz.bibliotecapessoal.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
        Instant expiracao = agora.plusMillis(expirationMs);

        return JWT.create()
                .withSubject(usuario.getEmail())
                .withIssuer(issuer)
                .withIssuedAt(agora)
                .withExpiresAt(expiracao)
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
}
