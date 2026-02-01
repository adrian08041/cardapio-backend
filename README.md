# 🍔 Cardápio Pro - Backend API

Backend RESTful para plataforma de Food Service desenvolvido com **Spring Boot 3** e **Java 21**.

## 🚀 Tecnologias

- **Java 21** - LTS
- **Spring Boot 3.5** - Framework
- **Spring Security** - Autenticação JWT
- **Spring Data JPA** - Persistência
- **PostgreSQL 16** - Banco de dados
- **Flyway** - Migrations
- **SpringDoc OpenAPI** - Documentação Swagger
- **Docker** - Containerização
- **Lombok** - Redução de boilerplate

## 📋 Funcionalidades

- ✅ **Catálogo**: Categorias, Produtos e Addons
- ✅ **Pedidos**: Criação, acompanhamento e histórico
- ✅ **Cupons**: Descontos percentuais e fixos
- ✅ **Fidelidade**: Programa de pontos e tiers
- ✅ **Autenticação**: JWT com refresh token
- ✅ **Configurações**: Horários, delivery e PIX

## 🏃 Quick Start

### Pré-requisitos

- Docker e Docker Compose
- Java 21 (para desenvolvimento local)
- Maven 3.9+

### Com Docker (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/cardapio-backend.git
cd cardapio-backend

# Inicie os containers
docker-compose up -d

# Verifique os logs
docker-compose logs -f api
```

### Desenvolvimento Local

```bash
# Inicie apenas o PostgreSQL
docker-compose up -d postgres

# Execute a aplicação
./mvnw spring-boot:run
```

## 🔗 URLs

| Serviço         | URL                                   |
| --------------- | ------------------------------------- |
| API             | http://localhost:8080                 |
| Swagger UI      | http://localhost:8080/swagger-ui.html |
| API Docs (JSON) | http://localhost:8080/v3/api-docs     |
| PgAdmin         | http://localhost:5050                 |

### Credenciais PgAdmin

- **Email**: admin@cardapio.com
- **Password**: admin123

## 🔐 Autenticação

A API usa JWT (JSON Web Tokens) para autenticação.

### Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin",
    "email": "admin@cardapio.com",
    "password": "admin123",
    "role": "ADMIN"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@cardapio.com",
    "password": "admin123"
  }'
```

### Usar Token

```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer {seu_token}"
```

## 📂 Estrutura do Projeto

```
src/main/java/com/cardapiopro/
├── config/          # Configurações (Security, OpenAPI)
├── controller/      # Controllers REST
├── dto/             # DTOs (Request/Response)
├── entity/          # Entidades JPA
├── exception/       # Exceções customizadas
├── repository/      # Repositórios JPA
├── security/        # JWT e filtros de segurança
└── service/         # Lógica de negócio

src/main/resources/
├── db/migration/    # Scripts Flyway
├── db/seed/         # Dados de teste
└── application.yaml # Configurações
```

## 🔒 Roles e Permissões

| Role          | Descrição                             |
| ------------- | ------------------------------------- |
| `CUSTOMER`    | Cliente - pode fazer pedidos          |
| `KITCHEN`     | Cozinha - pode preparar pedidos       |
| `ADMIN`       | Administrador - acesso total          |
| `SUPER_ADMIN` | Super Admin - configurações avançadas |

## 📊 Endpoints Principais

### Autenticação

- `POST /api/v1/auth/register` - Registrar usuário
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

### Catálogo

- `GET /api/v1/categories` - Listar categorias
- `GET /api/v1/products` - Listar produtos
- `GET /api/v1/products/{slug}` - Detalhes do produto

### Pedidos

- `POST /api/v1/orders` - Criar pedido
- `GET /api/v1/orders/{id}` - Detalhes do pedido
- `PATCH /api/v1/orders/{id}/status` - Atualizar status

### Fidelidade

- `GET /api/v1/loyalty/balance/{customerId}` - Saldo de pontos
- `POST /api/v1/loyalty/redeem/{customerId}` - Resgatar pontos

### Configurações

- `GET /api/v1/settings` - Configurações da loja
- `PUT /api/v1/settings` - Atualizar configurações

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Executar com cobertura
./mvnw test jacoco:report
```

## 📦 Build

```bash
# Build sem testes
./mvnw clean package -DskipTests

# Build com Docker
docker build -t cardapio-api .
```

## 🌐 Deploy

### Variáveis de Ambiente (Produção)

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/cardapio_pro
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=your-production-secret-256-bits
```

## 📝 Licença

Este projeto está sob a licença MIT.

---

Desenvolvido com ❤️ para o **Cardápio Pro**
