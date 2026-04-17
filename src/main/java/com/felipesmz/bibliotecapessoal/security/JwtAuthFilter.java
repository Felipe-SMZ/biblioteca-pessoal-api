package com.felipesmz.bibliotecapessoal.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // Rotas públicas
        return (path.equals("/auth/login") && method.equals("POST")) ||
                (path.equals("/usuarios") && method.equals("POST"));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        log.info("🔐 Iniciando filtro JWT para: {} {}", request.getMethod(), request.getRequestURI());

        // 🔹 Sem token → segue fluxo normal
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            log.warn("⚠️ Header Authorization ausente ou inválido");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            log.info("📌 Token recebido");

            String email = jwtService.validarToken(token).getSubject();
            log.info("📧 Email extraído do token: {}", email);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.info("👤 Usuário carregado: {}", userDetails.getUsername());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("✅ Usuário autenticado com sucesso");
            }

        } catch (JWTVerificationException e) {
            log.error("❌ Token inválido ou expirado: {}", e.getMessage());

        } catch (RuntimeException e) {
            log.error("❌ Erro ao autenticar usuário: {}", e.getMessage());
        }

        // 🔥 Sempre continua o fluxo
        filterChain.doFilter(request, response);
    }
}