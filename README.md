# Biblioteca Pessoal — API

API REST para gerenciar uma biblioteca pessoal com autenticação JWT.

Este repositório contém a implementação do desafio descrito em `DESAFIO.md`.

Principais objetivos implementados:

- Cadastro e autenticação de usuários (JWT)
- Criação e gerenciamento de livros por usuário
- Regras de negócio básicas aplicadas ao módulo de livros

---

## Sumário

- Visão geral e stack
- Como rodar localmente
- Variáveis de ambiente e configuração
- Endpoints e exemplos de uso
- Observações sobre segurança e melhorias recomendadas

---

## Stack

- Java 17+
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Maven (com wrapper `mvnw`)

---

## Executando localmente

Pré-requisitos:

- Java 17+
- MySQL (ou outra fonte compatível configurada em `application.properties`)
- Maven (opcional: use o wrapper `mvnw.cmd` no Windows)

Passos rápidos (Windows PowerShell):

```powershell
# Compila
.\mvnw.cmd clean package -DskipTests

# Executa a aplicação
.\mvnw.cmd spring-boot:run
```

A aplicação sobe por padrão em: http://localhost:8080

Se preferir executar o JAR gerado:

```powershell
java -jar target\biblioteca-pessoal-0.0.1-SNAPSHOT.jar
```

---

## Configuração (variáveis / application.properties)

Use `src/main/resources/application-example.properties` como referência. As principais propriedades:

- spring.datasource.url (ex.: jdbc:mysql://localhost:3306/biblioteca_db)
- spring.datasource.username
- spring.datasource.password

- jwt.secret (chave HMAC — mínimo 32 caracteres)
- jwt.expiration-ms (tempo em ms, ex: 86400000 = 24h)
- jwt.issuer

Você pode usar variáveis de ambiente para sobrescrever os valores padrão (veja `application-example.properties`).

---

## Endpoints principais

Autenticação / Usuário (públicos):

- POST /usuarios — cadastrar usuário
- POST /auth/login — login, retorna token JWT

Regras: as demais rotas exigem Header `Authorization: Bearer <token>`

Livros (protegido):

- GET /livros — listar livros do usuário autenticado
- POST /livros — criar novo livro (pertence ao usuário autenticado)
- GET /livros/{id} — obter livro (somente se pertencer ao usuário)
- PUT /livros/{id} — atualizar dados do livro (somente se pertencer ao usuário)
- PATCH /livros/{id}/status — atualizar páginas lidas / status
- PATCH /livros/{id}/avaliacao — avaliar livro concluído
- DELETE /livros/{id} — remover livro (somente se pertencer ao usuário)
- GET /livros/estatisticas — estatísticas do usuário

Consulte os DTOs em `src/main/java/com/felipesmz/bibliotecapessoal/dto` para os formatos esperados.

---

## Exemplos (curl)

# 1) Cadastrar usuário

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Fulano","email":"fulano@example.com","senha":"minhaSenha"}'
```

# 2) Login (retorna token)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"fulano@example.com","senha":"minhaSenha"}'
```

# 3) Usar token nas rotas protegidas

```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" http://localhost:8080/livros
```

Exemplos de payloads para livros (ver DTOs para validações):

POST /livros

```json
{
  "titulo": "O Senhor dos Anéis",
  "autor": "J.R.R. Tolkien",
  "genero": "Fantasia",
  "totalPaginas": 1178
}
```

PATCH /livros/{id}/status

```json
{
  "paginasLidas": 200
}
```

PATCH /livros/{id}/avaliacao

```json
{
  "avaliacao": 5
}
```

---

## Segurança e observações importantes (análise)

Resumo das verificações relevantes feitas no código:

- O `LivroController` obtém o email do usuário autenticado via `SecurityContextHolder` e resolve o `usuarioId` usando `UsuarioRepository.findByEmail(email)`.
- Todos os métodos do `LivroService` usam queries que filtram por `usuarioId` (ex.: `findByIdAndUsuarioId`, `findAllByUsuarioId`, `countByUsuarioIdAndStatus`). Isso impede que um usuário acesse livros de outro, desde que `usuarioId` passado seja o do usuário autenticado.

Potenciais pontos de melhoria/risco identificados:

1. Tratamento de exceções inconsistentes: algumas situações lançam `ResponseStatusException` (boas — retornam 4xx), outras usam `RuntimeException` com mensagens como "Livro não encontrado" ou "Usuário não encontrado". Isso causa 500 Internal Server Error. Recomenda-se padronizar para lançar `ResponseStatusException(HttpStatus.NOT_FOUND, "...")` ou criar exceções customizadas mapeadas pelo `@ControllerAdvice`.

2. `getUsuarioIdAutenticado()` assume que `SecurityContextHolder.getContext().getAuthentication()` e o `principal` não são nulos e que o principal é `UserDetails`. Em geral isso é verdade para requests autenticadas, mas uma verificação defensiva e mensagens claras (`401 Unauthorized`) melhoram a robustez.

3. O `JwtAuthFilter` registra logs informativos. Evite logar o token em texto claro em produção.

4. Validações de regras de negócio estão implementadas em grande parte (ex.: páginas lidas não podem exceder total; avaliação só para concluídos). Rever mensagens e códigos HTTP para consistência.

5. Recomendado remover exposição acidental de dados sensíveis em logs (senhas não são expostas nas respostas — DTOs de saída não incluem senha).

---

## Melhorias sugeridas (próximos passos)

- Padronizar exceções: criar `NotFoundException`, `AccessDeniedException`, `BadRequestException` e mapear no `GlobalExceptionHandler` para retornar JSON consistente.
- Melhorar `getUsuarioIdAutenticado()` para lançar `ResponseStatusException(HttpStatus.UNAUTHORIZED)` quando o principal não estiver presente.
- Adicionar testes unitários para `LivroService` cobrindo regras de negócio (paginas lidas, status, avaliação).
- Adicionar testes de integração (com banco H2 ou Testcontainers) para endpoints críticos.
- Considerar usar claim `userId` do token (já presente) para evitar uma consulta adicional ao banco ao resolver o usuário autenticado.

---

## Arquivos úteis

- `DESAFIO.md` — escopo do desafio
- `src/main/resources/application-example.properties` — exemplo de configuração

---

Se quiser, posso abrir um PR sugerindo mudanças pequenas e seguras (ex.: padronizar exceções e ajustar `getUsuarioIdAutenticado`) — diga quais pontos você prefere que eu altere automaticamente.
