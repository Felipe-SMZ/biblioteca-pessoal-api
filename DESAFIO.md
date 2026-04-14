# 🚀 Desafio: API REST de Biblioteca Pessoal

## 📋 Contexto

Uma editora independente quer um sistema simples para seus leitores cadastrarem e acompanharem os livros que já leram, estão lendo ou querem ler. Você vai construir o **backend** dessa plataforma.

---

## 🎯 Requisitos Funcionais

### Entidades

**Usuário**

| Campo | Tipo |
|-------|------|
| `id` | Long (PK) |
| `nome` | String |
| `email` | String (único) |
| `senha` | String |
| `dataCriacao` | LocalDateTime |

**Livro**

| Campo | Tipo |
|-------|------|
| `id` | Long (PK) |
| `titulo` | String |
| `autor` | String |
| `genero` | String |
| `totalPaginas` | Integer |
| `paginasLidas` | Integer |
| `status` | Enum: `QUERO_LER`, `LENDO`, `CONCLUIDO` |
| `avaliacao` | Integer (1 a 5, opcional) |
| `dataCriacao` | LocalDateTime |
| `usuario` | ManyToOne → Usuário |

---

## 🔗 Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/usuarios` | Cadastrar usuário |
| `POST` | `/auth/login` | Login (retorna token simples) |
| `GET` | `/livros` | Listar livros do usuário logado |
| `POST` | `/livros` | Adicionar livro à biblioteca |
| `GET` | `/livros/{id}` | Buscar livro por ID |
| `PUT` | `/livros/{id}` | Atualizar dados do livro |
| `PATCH` | `/livros/{id}/status` | Atualizar status e páginas lidas |
| `DELETE` | `/livros/{id}` | Remover livro da biblioteca |
| `GET` | `/livros/estatisticas` | Ver resumo da biblioteca |

### Exemplo de resposta — `GET /livros/estatisticas`

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

## 📐 Regras de Negócio

1. Um usuário **só pode ver, editar e deletar seus próprios livros**
2. `paginasLidas` não pode ser maior que `totalPaginas`
3. O status **só pode avançar** — não é permitido voltar (ex: `CONCLUIDO → LENDO` é inválido)
4. `avaliacao` só pode ser preenchida se o status for `CONCLUIDO` — caso contrário, retorne erro adequado
5. Quando o status mudar para `CONCLUIDO`, o campo `paginasLidas` deve ser automaticamente igualado a `totalPaginas`
6. A **senha nunca deve aparecer** em nenhuma resposta da API
7. Email duplicado deve retornar erro com mensagem clara

---

## 🛠️ Stack

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- MySQL — banco de dados: `biblioteca_db`
- Maven

> **Auth:** Basic Auth ou token UUID em memória — sem JWT por enquanto.  
> Exemplo simples: um `Map<String, String>` (token → email) em um `@Component` já resolve.

---

## 📦 Entregáveis

- [ ] Código no GitHub com repositório público
- [ ] `README.md` com instruções para rodar o projeto localmente
- [ ] Tabelas criadas via `spring.jpa.hibernate.ddl-auto` ou script SQL
- [ ] Coleção de requisições (Postman, Insomnia ou arquivo `.http`)

---

## ✅ Critérios de Avaliação

| Critério | Peso |
|----------|------|
| Endpoints funcionando corretamente | ⭐⭐⭐ |
| Regras de negócio implementadas | ⭐⭐⭐ |
| Tratamento de erros (400, 403, 404) | ⭐⭐ |
| Organização em camadas (`controller / service / repository`) | ⭐⭐ |
| README e coleção de testes | ⭐ |

---

## 💡 Dicas

- Use **DTOs** separados para entrada e saída — nunca exponha a entidade JPA diretamente nas respostas
- Use `@ControllerAdvice` + `@ExceptionHandler` para centralizar o tratamento de erros
- Filtre sempre pelo usuário autenticado antes de qualquer operação no banco
- Crie exceções customizadas (`LivroNaoEncontradoException`, `AcessoNegadoException`, etc.) para deixar o código mais expressivo

---

## 🗂️ Sugestão de Estrutura de Pacotes

```
src/
└── main/
    └── java/
        └── com/seuprojeto/biblioteca/
            ├── controller/
            ├── service/
            ├── repository/
            ├── model/
            ├── dto/
            ├── exception/
            └── config/
```

---

*Quando estiver pronto, apresente o código para receber um code review completo com feedback detalhado.* 🎯