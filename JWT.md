# Manual JWT - Biblioteca Pessoal

Este documento e um guia de consulta rapida para relembrar como o JWT foi implementado neste projeto.

## Objetivo

Implementar autenticacao stateless com JWT para:

- permitir login por email e senha
- gerar token no `POST /auth/login`
- exigir token nas rotas protegidas
- manter cada requisicao sem sessao no servidor

## Estado atual do projeto

Hoje o projeto ja possui:

- `AuthController` com `POST /auth/login`
- `AuthService` com autenticacao via `AuthenticationManager`
- `JwtService` para gerar e validar token
- `JwtAuthFilter` para ler `Authorization: Bearer ...`
- `CustomUserDetailsService` para buscar usuario por email
- `SecurityConfig` stateless com rotas publicas e protegidas

## Estrutura de pacotes (JWT)

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
└── config
    └── SecurityConfig.java
```

## Fluxo JWT (resumo mental)

1. Cliente chama `POST /auth/login` com email e senha
2. `AuthService` autentica com `AuthenticationManager`
3. Se valido, `JwtService` gera token
4. Cliente envia token no header `Authorization`
5. `JwtAuthFilter` valida token e coloca usuario no contexto de seguranca
6. Rotas protegidas executam normalmente

Se token for invalido, expirado ou usuario nao existir mais, retorna `401`.

## Passo a passo de implementacao

## 1) DTOs de login

`LoginRequest` deve validar entrada:

- `@NotBlank` para email e senha
- `@Email` para formato do email

`LoginResponse` retorna:

- `token`
- `tipo` (`Bearer`)

## 2) Servico de token (`JwtService`)

Responsavel por:

- gerar token (`gerarToken`)
- validar token (`validarToken`)

Claims usadas no projeto:

- `subject`: email do usuario
- `userId`: id do usuario
- `issuer`: valor configurado em `jwt.issuer`
- expiracao: `jwt.expiration-ms`

Configuracao esperada em properties:

```properties
jwt.secret=${JWT_SECRET:exemplo_de_chave_secreta_muito_longa_e_segura_123456}
jwt.expiration-ms=${JWT_EXPIRATION:86400000}
jwt.issuer=${JWT_ISSUER:biblioteca-pessoal-api}
```

## 3) Carregar usuario para autenticacao (`CustomUserDetailsService`)

- busca usuario por email no `UsuarioRepository`
- se nao encontrar, lanca `UsernameNotFoundException`
- retorna `UserDetails` com senha criptografada do banco

Observacao importante:

- usar `.roles("USER")` (o prefixo `ROLE_` ja e aplicado internamente)

## 4) Filtro JWT (`JwtAuthFilter`)

Papel do filtro:

- ignorar rotas publicas (`/auth/login` e `POST /usuarios`)
- ler header `Authorization`
- validar prefixo `Bearer `
- validar token no `JwtService`
- carregar usuario e setar autenticacao no `SecurityContextHolder`

Tratamento de erro atual:

- token invalido/expirado -> `401` com JSON
- token valido, mas usuario nao encontrado -> `401` com JSON

## 5) Configuracao de seguranca (`SecurityConfig`)

Regras principais no projeto:

- `SessionCreationPolicy.STATELESS`
- `POST /auth/login` e `POST /usuarios` sao publicos
- demais rotas exigem autenticacao
- `AuthenticationProvider` com `DaoAuthenticationProvider`
- `PasswordEncoder` com `BCryptPasswordEncoder`
- `JwtAuthFilter` antes de `UsernamePasswordAuthenticationFilter`
- sem autenticacao valida -> resposta `401`

## 6) Login (`AuthService` + `AuthController`)

`AuthService.login(...)`:

1. autentica email/senha com `authenticationManager.authenticate(...)`
2. busca usuario no banco
3. gera token no `JwtService`
4. retorna `LoginResponse(token, "Bearer")`

`AuthController` expoe:

- `POST /auth/login`

## Como testar rapido

## 1) Criar usuario

```http
POST /usuarios
Content-Type: application/json

{
  "nome": "Felipe",
  "email": "felipe@email.com",
  "senha": "123456"
}
```

## 2) Fazer login

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

## 3) Chamar rota protegida

```http
PUT /usuarios/1
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

## Erros comuns (e causa)

- `401 Unauthorized`
  - token ausente
  - token expirado/invalido
  - token de outro ambiente (secret diferente)
  - usuario do token nao existe mais

- `403 Forbidden`
  - autenticado, mas sem permissao para acao

- `415 Unsupported Media Type`
  - request enviada sem `Content-Type: application/json`

- erro de parser HTTP com caracteres estranhos
  - normalmente chamada em `https://localhost:8080` sem SSL configurado
  - use `http://localhost:8080`

## Checklist de implementacao JWT

- [x] DTOs de login (`LoginRequest`, `LoginResponse`)
- [x] `JwtService` para gerar/validar token
- [x] `CustomUserDetailsService`
- [x] `JwtAuthFilter`
- [x] `SecurityConfig` stateless
- [x] `AuthService`
- [x] `AuthController`

## Proximo passo apos JWT

JWT esta pronto para autenticar usuario.

Ainda falta concluir o modulo de livros com isolamento por usuario:

- `GET /livros`
- `POST /livros`
- `GET /livros/{id}`
- `PUT /livros/{id}`
- `PATCH /livros/{id}/status`
- `DELETE /livros/{id}`
- `GET /livros/estatisticas`

## Regra para nao esquecer

Quando bater duvida, relembre esta frase:

> Login autentica credencial, JWT identifica o usuario nas proximas requisicoes.
