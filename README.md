# Produção API

API REST para gestão de ordens de produção, desenvolvida com Spring Boot 3.4.1 e Java 21. O sistema permite controlar o ciclo completo de uma ordem de produção — da criação ao encerramento — com rastreamento de lotes, consumo de insumos, monitoramento de máquinas em tempo real via WebSocket e autenticação por JWT com controle de perfis de acesso.

---

## Funcionalidades

- **Ordens de produção** — crie, acompanhe e encerre ordens com controle de quantidade planejada vs. produzida
- **Lotes** — registro de lotes vinculados a insumos e ordens, com rastreabilidade completa
- **Insumos** — cadastro e consulta de materiais utilizados na produção
- **Máquinas** — gerenciamento de máquinas com atualizações em tempo real via WebSocket
- **Usuários e autenticação** — login com JWT, cadastro restrito por perfil e controle de acesso por roles (`OPERADOR`, `GESTOR`, `ADMINISTRADOR`)

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Banco de dados | PostgreSQL 16 |
| Migrações | Flyway |
| Segurança | Spring Security + JWT (jjwt 0.12.6) |
| Tempo real | Spring WebSocket |
| Documentação | SpringDoc OpenAPI 2.7.0 (Swagger UI) |
| Containerização | Docker + Docker Compose |

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose (para rodar com containers)
- PostgreSQL 16 (caso prefira rodar localmente sem Docker)

---

## Como rodar

### Com Docker Compose (recomendado)

Sobe a API e o banco de dados juntos com um único comando:

```bash
docker-compose up --build
```

A API ficará disponível em `http://localhost:8080/api`.

> Para personalizar a chave JWT em ambiente de produção, defina a variável de ambiente `JWT_SEGREDO` com uma string de pelo menos 32 caracteres.

### Localmente com Maven

1. Certifique-se de ter um PostgreSQL rodando com um banco chamado `producao_db`.
2. Configure as variáveis de ambiente (ou deixe os valores padrão do perfil `dev`):

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/producao_db
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres
```

3. Suba a aplicação:

```bash
mvn spring-boot:run
```

---

## Perfis de ambiente

| Perfil | Uso | Observações |
|---|---|---|
| `dev` | Desenvolvimento local | SQL exibido no console, logs detalhados |
| `prod` | Produção | Swagger desabilitado, pool de conexões configurado, logs reduzidos |

Defina o perfil via variável de ambiente:

```bash
export SPRING_PROFILES_ACTIVE=prod
```

---

## Documentação da API

Com o perfil `dev` ativo, a documentação interativa está disponível em:

- **Swagger UI:** `http://localhost:8080/api/swagger-ui`
- **OpenAPI JSON:** `http://localhost:8080/api/docs`

---

## Endpoints principais

### Autenticação — `/api/auth`

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/auth/login` | Autentica e retorna o token JWT | Público |
| `POST` | `/auth/cadastro` | Cadastra novo usuário | `ADMINISTRADOR` |

### Ordens de produção — `/api/orders`

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/orders` | Lista todas as ordens (filtrável por status) | Autenticado |
| `GET` | `/orders/{id}` | Busca uma ordem por ID | Autenticado |
| `POST` | `/orders` | Cria nova ordem de produção | `GESTOR`, `ADMINISTRADOR` |
| `PATCH` | `/orders/{id}/iniciar` | Inicia o lote da ordem | Autenticado |
| `PATCH` | `/orders/{id}/finalizar` | Finaliza o lote da ordem | Autenticado |
| `DELETE` | `/orders/{id}` | Cancela uma ordem | `ADMINISTRADOR` |

### Demais recursos

| Prefixo | Descrição |
|---|---|
| `/api/insumos` | CRUD de insumos |
| `/api/lotes` | Consulta e registro de lotes |
| `/api/maquinas` | Gerenciamento de máquinas |
| `/api/usuarios` | Gerenciamento de usuários |

---

## Estrutura do projeto

```
src/
└── main/
    ├── java/com/producao/
    │   ├── config/          # Configurações de CORS, segurança, WebSocket e OpenAPI
    │   ├── domain/          # Regras de negócio por domínio (insumo, lote, máquina, ordem, usuário)
    │   ├── infra/           # Tratamento de exceções e resposta padrão
    │   └── security/        # Filtro JWT, modelo e serviço de autenticação
    └── resources/
        ├── application.yml          # Configuração base
        ├── application-dev.yml      # Overrides para desenvolvimento
        ├── application-prod.yml     # Overrides para produção
        └── db/migration/            # Scripts Flyway (V1 a V6)
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/producao_db` | URL de conexão com o banco |
| `DATABASE_USER` | `postgres` | Usuário do banco |
| `DATABASE_PASSWORD` | `postgres` | Senha do banco |
| `JWT_SEGREDO` | *(chave de dev)* | Chave secreta para assinar os tokens JWT |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil ativo da aplicação |

---

## Rodando os testes

```bash
mvn test
```

---

## Licença

Este projeto está licenciado sob os termos do arquivo [LICENSE](LICENSE).

