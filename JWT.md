# Manual simples de implementação de JWT no projeto Biblioteca Pessoal

> **Objetivo deste documento:** servir como uma anotação prática para eu lembrar, no futuro, como implementar autenticação com JWT no projeto.
>
> **Ideia principal:** o usuário faz login com email e senha, recebe um token JWT e passa esse token nas próximas requisições.

---

## 1. O que já existe no projeto

Pelo código atual, já tenho:

- `Usuario` em `com.felipesmz.bibliotecapessoal.model.Usuario`
- `Livro` em `com.felipesmz.bibliotecapessoal.model.Livro`
- `UsuarioRepository` com `findByEmail(String email)`
- `UsuarioService` com criptografia de senha usando BCrypt
- `SecurityConfig` ainda liberando todas as rotas
- dependência de JWT no `pom.xml`

Ou seja: a base já está pronta. Falta montar a parte de login e a validação do token.

---

## 2. O que o JWT vai resolver

JWT vai servir para:

1. saber se o usuário está autenticado;
2. evitar que ele tenha que mandar email e senha em toda requisição;
3. identificar quem é o dono dos dados;
4. proteger rotas de `usuario` e `livro`.

A lógica vai ser assim:

- o usuário faz `POST /auth/login`;
- a API valida email e senha;
- se estiver certo, gera um token;
- nas próximas requisições, o cliente envia:

```http
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## 3. Regras simples que eu preciso seguir

Antes de codar, preciso lembrar destas regras:

- o token precisa expirar;
- o segredo do JWT não deve ficar hardcoded;
- o endpoint de login deve ser público;
- as outras rotas devem exigir autenticação;
- nunca retornar senha na resposta;
- para dados privados, buscar sempre pelo usuário autenticado.

---

## 4. Estrutura de pacotes que eu vou criar

Vou manter a estrutura simples, sem inventar muita coisa.

```text
com.felipesmz.bibliotecapessoal
├── auth
│   ├── controller
│   │   └── AuthController.java
│   ├── dto
│   │   ├── LoginRequest.java
│   │   └── LoginResponse.java
│   └── service
│       └── AuthService.java
├── security
│   ├── JwtService.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
├── config
│   └── SecurityConfig.java
└── (ajustes nos pacotes que já existem)
```

### Por que essa estrutura?

Porque fica fácil entender:

- `controller` recebe a requisição;
- `service` faz a regra de negócio;
- `dto` guarda os dados da entrada e da saída;
- `security` guarda tudo que é autenticação.

---

## 5. Passo a passo da implementação

### Passo 1 — criar os DTOs do login

Esses DTOs são as classes que recebem e devolvem dados na API.

#### `LoginRequest`

```java
package com.felipesmz.bibliotecapessoal.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
```

#### `LoginResponse`

```java
package com.felipesmz.bibliotecapessoal.auth.dto;

public class LoginResponse {

    private String token;
    private String tipo;

    public LoginResponse(String token, String tipo) {
        this.token = token;
        this.tipo = tipo;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }
}
```

**Ideia importante:** a resposta do login só devolve token e tipo. Não devolve senha nem dados sensíveis.

---

### Passo 2 — criar o serviço do JWT

Esse serviço vai cuidar de gerar e validar token.

#### `JwtService`

```java
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
        Instant expiracao = agora.plusMillis(expirationMs);

        return JWT.create()
                .withSubject(usuario.getEmail())
                .withClaim("userId", usuario.getId())
                .withIssuer(issuer)
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(expiracao))
                .sign(Algorithm.HMAC256(secret));
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
```

### O que esse código faz?

- `gerarToken(...)` cria o JWT;
- `validarToken(...)` confere se o token é válido;
- `subject` guarda o email;
- `userId` guarda o id do usuário;
- `expirationMs` define quando o token expira.

### Onde colocar as configurações

No `src/main/resources/application.properties`:

```properties
jwt.secret=coloque-um-segredo-forte-aqui
jwt.expiration-ms=86400000
jwt.issuer=biblioteca-pessoal-api
```

> Dica: depois, o ideal é trocar isso por variável de ambiente.

---

### Passo 3 — criar o `UserDetailsService`

O Spring Security precisa saber como buscar o usuário pelo email.

#### `CustomUserDetailsService`

```java
package com.felipesmz.bibliotecapessoal.security;

import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_USER")
                .build();
    }
}
```

### Por que usar email aqui?

Porque no meu projeto o email é único e é o dado mais natural para login.

---

### Passo 4 — criar o filtro JWT

O filtro lê o header `Authorization`, valida o token e coloca o usuário no contexto do Spring.

#### `JwtAuthFilter`

```java
package com.felipesmz.bibliotecapessoal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String email = jwtService.validarToken(token).getSubject();

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

### O que este filtro faz?

1. pega o token do header;
2. valida o token;
3. pega o email do token;
4. busca o usuário no banco;
5. marca a requisição como autenticada.

---

### Passo 5 — configurar o `SecurityConfig`

Aqui eu digo quais rotas são públicas e quais precisam de token.

#### `SecurityConfig` em `com.felipesmz.bibliotecapessoal.config`

```java
package com.felipesmz.bibliotecapessoal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/usuarios").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### O que essa configuração significa?

- `/auth/login` fica público;
- cadastro de usuário pode ficar público;
- todo o resto exige token;
- a aplicação não usa sessão, então é stateless.

---

### Passo 6 — criar o `AuthService`

Esse service faz o login e gera o token.

#### `AuthService`

```java
package com.felipesmz.bibliotecapessoal.auth.service;

import com.felipesmz.bibliotecapessoal.auth.dto.LoginRequest;
import com.felipesmz.bibliotecapessoal.auth.dto.LoginResponse;
import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import com.felipesmz.bibliotecapessoal.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token, "Bearer");
    }
}
```

### Fluxo do login

- recebo email e senha;
- o Spring valida as credenciais;
- se estiver tudo certo, busco o usuário;
- gero o token;
- devolvo a resposta.

---

### Passo 7 — criar o `AuthController`

Esse controller expõe a rota de login.

#### `AuthController`

```java
package com.felipesmz.bibliotecapessoal.auth.controller;

import com.felipesmz.bibliotecapessoal.auth.dto.LoginRequest;
import com.felipesmz.bibliotecapessoal.auth.dto.LoginResponse;
import com.felipesmz.bibliotecapessoal.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

---

## 6. Como proteger as rotas de `usuario` e `livro`

Aqui está o ponto principal: não basta só ter token. Eu preciso garantir que o usuário só veja o que é dele.

### Regra simples

Quando eu for buscar um livro ou usuário, eu não devo usar só o `id` da URL.

Eu devo buscar assim:

- `findByIdAndUsuarioId(...)`
- `findByUsuarioId(...)`

Isso evita que um usuário acesse dados de outro.

### Exemplo para `LivroRepository`

```java
package com.felipesmz.bibliotecapessoal.repository;

import com.felipesmz.bibliotecapessoal.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByUsuarioId(Long usuarioId);

    Optional<Livro> findByIdAndUsuarioId(Long id, Long usuarioId);
}
```

### Como salvar um livro com dono

No service de livro, eu pego o usuário autenticado e coloco no livro antes de salvar.

O importante aqui é lembrar que, no meu modelo atual, o `Livro` tem:

```java
private Usuario usuario;
```

Então eu preciso fazer algo como:

```java
livro.setUsuario(usuarioLogado);
```

e não `setUsuarioId(...)`.

---

## 7. Validação no DTO

A validação vai no DTO, não na entidade.

Exemplo:

- `@NotBlank` para campos obrigatórios;
- `@Email` para email;
- `@Size` para tamanho mínimo.

Isso deixa a API mais limpa e evita repetir validação em vários lugares.

---

## 8. O que fazer no `UsuarioService`

No meu projeto, o `UsuarioService` já faz criptografia da senha.

Então eu preciso manter isso:

- ao criar usuário, salvar senha com BCrypt;
- ao atualizar senha, criptografar de novo;
- nunca devolver senha na resposta.

### Sobre a validação de email

O método correto é algo parecido com isto:

```java
private void validarEmail(Long id, Usuario usuario) {
    usuarioRepository.findByEmail(usuario.getEmail())
            .ifPresent(usuarioExistente -> {
                if (!usuarioExistente.getId().equals(id)) {
                    throw new RuntimeException("E-mail já cadastrado");
                }
            });
}
```

Se o email já existe e não pertence ao mesmo usuário, eu bloqueio.

---

## 9. Como testar no Postman

### 1. Cadastrar usuário

```http
POST /usuarios
Content-Type: application/json

{
  "nome": "Felipe",
  "email": "felipe@email.com",
  "senha": "123456"
}
```

### 2. Fazer login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "felipe@email.com",
  "senha": "123456"
}
```

Resposta esperada:

```json
{
  "token": "...",
  "tipo": "Bearer"
}
```

### 3. Chamar rota protegida

```http
GET /livros
Authorization: Bearer SEU_TOKEN
```

Se o token estiver certo, a API responde normalmente.

Se estiver errado ou ausente, deve retornar `401`.

---

## 10. Resumo bem curto do fluxo

1. cadastro do usuário;
2. senha salva com BCrypt;
3. login com email e senha;
4. geração do JWT;
5. envio do token nas próximas requisições;
6. filtro valida o token;
7. API identifica o usuário logado;
8. serviços de livro e usuário usam o id do usuário autenticado para filtrar os dados.

---

## 11. O que eu preciso lembrar depois

### Se eu esquecer, basta revisar esta ordem:

- criar DTOs do login;
- criar `JwtService`;
- criar `CustomUserDetailsService`;
- criar `JwtAuthFilter`;
- ajustar `SecurityConfig`;
- criar `AuthService`;
- criar `AuthController`;
- proteger `LivroRepository` e `UsuarioRepository` com filtro por usuário;
- testar com Postman.

---

## 12. O que eu simplifiquei neste manual

Para ficar fácil de entender, eu evitei:

- arquitetura exageradamente grande;
- muitos pacotes desnecessários;
- uso de Lombok;
- exemplos avançados demais;
- conceitos que ainda não ajudam na primeira implementação.

Esse documento é para me fazer lembrar **como implementar**, não para virar uma teoria enorme.

---

## 13. Observação final

Este guia é para eu consultar no futuro quando eu já tiver o básico de Java e quiser relembrar o passo a passo do JWT.

Se eu seguir a ordem desta documentação, consigo implementar a autenticação sem me perder.

