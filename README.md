# Biblioteca Pessoal API

API REST para gerenciamento de biblioteca pessoal com autenticacao JWT.

## Status do projeto

- `usuarios` e `auth/login` implementados
- autenticacao JWT funcionando
- regras de livro ainda em desenvolvimento

## Objetivo

Permitir que cada usuario:

- crie sua conta
- faca login
- use token JWT nas rotas protegidas
- (proximo passo) gerencie apenas os proprios livros

## Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- Java JWT (`com.auth0:java-jwt`)

## Estrutura principal

```text
src/main/java/com/felipesmz/bibliotecapessoal
├── auth
│   ├── controller
│   ├── dto
│   └── service
├── config
├── controller
├── dto
├── mapper
├── model
├── repository
├── security
└── service
```

## Funcionalidades atuais

- cadastro de usuario com validacoes de DTO
- senha criptografada com `PasswordEncoder`
- login com email e senha
- geracao de token JWT no login
- validacao de token por filtro (`JwtAuthFilter`)
- rotas publicas:
  - `POST /auth/login`
  - `POST /usuarios`
- demais rotas exigem token

## Requisitos locais

- JDK 21+
- Maven (ou usar `mvnw`)
- MySQL rodando

## Configuracao de ambiente

Use o arquivo `src/main/resources/application-example.properties` como referencia.

As configuracoes suportam variaveis de ambiente com valor padrao.

### Variaveis de banco

- `DB_HOST` (padrao: `localhost`)
- `DB_PORT` (padrao: `3306`)
- `DB_NAME` (padrao: `biblioteca_db`)
- `DB_USERNAME` (padrao: `root`)
- `DB_PASSWORD` (padrao definido no seu `application.properties` local)

### Variaveis de JWT

- `JWT_SECRET`
- `JWT_EXPIRATION` (em ms, ex: `86400000`)
- `JWT_ISSUER`

## Como executar o projeto

```powershell
./mvnw.cmd clean compile
./mvnw.cmd spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

## Fluxo de autenticacao (JWT)

1. Cliente chama `POST /auth/login`
2. API valida credenciais
3. API retorna token
4. Cliente envia token nas rotas protegidas:

```http
Authorization: Bearer SEU_TOKEN_AQUI
```

Se o token estiver invalido/expirado, a API retorna `401`.

## Endpoints atuais

### Publicos

| Metodo | Rota | Descricao |
|---|---|---|
| POST | `/usuarios` | Cadastrar usuario |
| POST | `/auth/login` | Login e retorno do token |

### Protegidos

| Metodo | Rota | Descricao |
|---|---|---|
| GET | `/usuarios/{id}` | Buscar usuario por id |
| PUT | `/usuarios/{id}` | Atualizar usuario |
| DELETE | `/usuarios/{id}` | Remover usuario |

## Exemplos de requisicao

### 1) Cadastrar usuario

```http
POST /usuarios
Content-Type: application/json
```

```json
{
  "nome": "Felipe Souza",
  "email": "felipe@email.com",
  "senha": "123456"
}
```

### 2) Login

```http
POST /auth/login
Content-Type: application/json
```

```json
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

### 3) Atualizar usuario (rota protegida)

```http
PUT /usuarios/1
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

```json
{
  "nome": "Felipe Atualizado",
  "email": "felipe@email.com",
  "senha": "12345678"
}
```

## Erros comuns

- `401 Unauthorized`: token ausente, invalido, expirado ou usuario do token nao encontrado
- `403 Forbidden`: regra de autorizacao negou acesso
- `415 Unsupported Media Type`: request sem `Content-Type: application/json`

## Proximas implementacoes (modulo de livros)

### API de livros

- [ ] Criar `LivroController`
- [ ] Criar `LivroService`
- [ ] Criar DTOs de entrada/saida de livro
- [ ] Implementar endpoints:
  - [ ] `GET /livros`
  - [ ] `POST /livros`
  - [ ] `GET /livros/{id}`
  - [ ] `PUT /livros/{id}`
  - [ ] `PATCH /livros/{id}/status`
  - [ ] `DELETE /livros/{id}`
  - [ ] `GET /livros/estatisticas`

### Regras de negocio pendentes

- [ ] Usuario so pode acessar os proprios livros
- [ ] `paginasLidas <= totalPaginas`
- [ ] Status so pode avancar (nao retrocede)
- [ ] `avaliacao` so quando status for `CONCLUIDO`
- [ ] Ao concluir livro, ajustar `paginasLidas = totalPaginas`

### Qualidade tecnica recomendada

- [ ] `@ControllerAdvice` para padronizar erros
- [ ] Testes unitarios para service
- [ ] Testes de integracao para endpoints
- [ ] Colecao do Insomnia/Postman versionada

## Documentos do repositorio

- `DESAFIO.md`: escopo original do desafio
- `JWT.md`: guia detalhado de implementacao JWT
- `src/main/resources/application-example.properties`: referencia de configuracao local

## Observacao

Este README descreve o estado atual do projeto e o proximo roadmap.
Quando o modulo de livros estiver concluido, atualize este documento para refletir os endpoints e regras finais.
