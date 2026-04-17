# 📚 Biblioteca Pessoal — API REST

API REST para gerenciamento de biblioteca pessoal, desenvolvida com Java 21 e Spring Boot.

---

## 🚀 Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- MySQL
- Maven

---

## ⚙️ Configuração

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```

Principais propriedades:

| Propriedade | Descrição | Exemplo |
|-------------|-----------|---------|
| `spring.datasource.url` | URL do banco | `jdbc:mysql://localhost:3306/biblioteca_db` |
| `spring.datasource.username` | Usuário do banco | `root` |
| `spring.datasource.password` | Senha do banco | `root` |
| `jwt.secret` | Chave HMAC (mín. 32 caracteres) | `minha-chave-super-secreta-aqui` |
| `jwt.expiration-ms` | Expiração do token em ms | `86400000` (24h) |
| `jwt.issuer` | Emissor do token | `biblioteca-pessoal-api` |

---

## ▶️ Como rodar localmente

**Pré-requisitos:**
- Java 21+
- MySQL rodando localmente
- Banco `biblioteca_db` criado

**Passos:**

```bash
# Windows
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

A aplicação sobe em: `http://localhost:8080`

Ou execute o JAR diretamente:

```bash
java -jar target/biblioteca-pessoal-0.0.1-SNAPSHOT.jar
```

---

## 🔗 Endpoints

### Públicos

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/usuarios` | Cadastrar usuário |
| `POST` | `/auth/login` | Login — retorna token JWT |

### Protegidos

> Todas as rotas abaixo exigem o header `Authorization: Bearer <token>`

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/livros` | Listar livros do usuário autenticado |
| `POST` | `/livros` | Adicionar livro à biblioteca |
| `GET` | `/livros/{id}` | Buscar livro por ID |
| `PUT` | `/livros/{id}` | Atualizar dados do livro |
| `PATCH` | `/livros/{id}/status` | Atualizar status e páginas lidas |
| `PATCH` | `/livros/{id}/avaliacao` | Avaliar livro concluído |
| `DELETE` | `/livros/{id}` | Remover livro |
| `GET` | `/livros/estatisticas` | Estatísticas da biblioteca |

---

## 📦 Exemplos de uso

### Cadastrar usuário

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Felipe","email":"felipe@example.com","senha":"minhasenha"}'
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"felipe@example.com","senha":"minhasenha"}'
```

### Adicionar livro

```bash
curl -X POST http://localhost:8080/livros \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "O Senhor dos Anéis",
    "autor": "J.R.R. Tolkien",
    "genero": "Fantasia",
    "totalPaginas": 1178
  }'
```

### Atualizar status

```bash
curl -X PATCH http://localhost:8080/livros/1/status \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"paginasLidas": 300}'
```

### Avaliar livro

```bash
curl -X PATCH http://localhost:8080/livros/1/avaliacao \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"avaliacao": 5}'
```

### Estatísticas

```bash
curl -H "Authorization: Bearer SEU_TOKEN" \
  http://localhost:8080/livros/estatisticas
```

Resposta:

```json
{
  "totalLivros": 10,
  "concluidos": 4,
  "lendo": 2,
  "quereLer": 4,
  "mediaAvaliacao": 4.2,
  "totalPaginasLidas": 1340
}
```

---

## 📐 Regras de negócio

- Cada usuário acessa apenas seus próprios livros
- `paginasLidas` não pode ser maior que `totalPaginas`
- O status só pode avançar — não é permitido voltar (ex: `CONCLUIDO → LENDO` é inválido)
- Avaliação só pode ser atribuída a livros com status `CONCLUIDO`
- Ao concluir um livro, `paginasLidas` é automaticamente igualado a `totalPaginas`
- A senha nunca é retornada em nenhum endpoint

---

## 🗂️ Estrutura do projeto

```
src/
└── main/
    └── java/
        └── com/felipesmz/bibliotecapessoal/
            ├── controller/
            ├── service/
            ├── repository/
            ├── model/
            ├── dto/
            ├── exception/
            └── config/
```

---

## 🔮 Melhorias futuras

- Padronização completa das respostas de erro com `@ControllerAdvice`
- Testes unitários para as regras de negócio no `LivroService`
- Testes de integração para os endpoints principais
- Paginação na listagem de livros
- Filtros por status, gênero e autor
- Documentação automática com Swagger / OpenAPI

---

## 📄 Arquivos úteis

- `DESAFIO.md` — escopo e requisitos do desafio
- `src/main/resources/application-example.properties` — exemplo de configuração