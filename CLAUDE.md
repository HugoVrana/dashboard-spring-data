# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build
./mvnw clean package

# Build (skip tests)
./mvnw clean package -DskipTests

# Run locally (port 8080)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run single test class
./mvnw test -Dtest=ClassName

# Generate Allure test report
./mvnw allure:serve
```

## Architecture Overview

Spring Boot 3.4.3 REST API backend for a dashboard application simulating a company (Acme) tracking invoices to customers. Uses JWT authentication validated against an external auth server.

### Core Flow
1. **Request arrives** → JwtGrantsFilter extracts Bearer token from Authorization header
2. **Token validation** → JJWT library validates signature using shared secret
3. **Extract grants** → JWT claims contain `grants` list (permissions)
4. **Set SecurityContext** → GrantsAuthentication created with username and grants
5. **Authorization** → `@PreAuthorize("hasAuthority('...')")` checks grants on endpoints

### Key Packages

- `config/` - SecurityConfig (JWT filter chain, CORS), WebConfig, LoggingConfig
- `controller/` - CustomersController, InvoicesController, UsersController, RevenuesController
- `service/` - CustomerService, InvoiceService, InvoiceSearchService, UserService, RevenueService
- `service/interfaces/` - Service interfaces (ICustomerService, IInvoiceService, etc.)
- `filter/JwtGrantsFilter` - Extracts JWT from Authorization header, validates, sets GrantsAuthentication
- `filter/RequestResponseCachingFilter` - Caches request/response for logging
- `model/entities/` - Customer, Invoice, User, Revenue, InvoiceSearchDocument (all with Audit for soft delete)
- `mapper/` - Entity-DTO mappers (CustomerMapper, InvoiceMapper, UserMapper, RevenueMapper)
- `mapper/interfaces/` - Mapper interfaces
- `dataTransferObject/` - DTOs organized by entity (InvoiceCreate, InvoiceRead, InvoiceUpdate, etc.)
- `repository/` - MongoDB repositories (ICustomersRepository, IInvoiceRepository, etc.)
- `exceptionhandlers/` - GlobalExceptionHandler with ProblemDetail responses
- `interceptor/` - ApiLoggingInterceptor for Grafana logging
- `authentication/` - GrantsAuthentication (custom Authentication implementation)

### Authorization Model

- JWT tokens issued by external auth server contain `grants` list in claims
- JwtGrantsFilter extracts grants and creates GrantsAuthentication
- Grants become GrantedAuthorities (e.g., `dashboard-invoices-read`, `dashboard-customers-read`)
- Endpoints protected with `@PreAuthorize("hasAuthority('grant-name')")`

**Grant naming convention:** `dashboard-{resource}-{action}`
- `dashboard-invoices-read`, `dashboard-invoices-create`, `dashboard-invoices-update`, `dashboard-invoices-delete`
- `dashboard-customers-read`
- `dashboard-users-read`, `dashboard-users-create`
- `dashboard-revenue-read`

### Database

MongoDB with collections: customers, invoices, invoices_search, users, revenues. All entities use Audit model from `com.dashboard:common` with soft delete pattern (`audit.deletedAt`).

**Entities:**
- `Customer` - _id, name, email, image_url, audit
- `Invoice` - _id, customer (DBRef), amount, date, status, audit
- `InvoiceSearchDocument` - Denormalized search collection with text indexes on customerName, customerEmail, status
- `User` - _id, name, email, password, audit
- `Revenue` - _id, month, revenue, audit

### External Dependencies

- `com.dashboard:common` - Shared Audit model, custom exceptions (ConflictException, ResourceNotFoundException, InvalidRequestException, NotFoundException), GrafanaHttpClient for logging
- JJWT 0.12.6 for JWT validation
- springdoc-openapi for API documentation (Swagger UI)
- Caffeine for caching
- Embedded MongoDB (flapdoodle) for tests
- DataFaker for test data generation
- Allure for test reporting

### Configuration Properties

Key properties in `application.properties`:
- `JWT.SECRET` - BASE64 encoded secret for HMAC JWT validation (shared with auth server)
- `spring.data.mongodb.uri` - MongoDB Atlas connection string
- `grafana.apiKey` - Grafana Loki API key for logging
- `grafana.url` - Grafana Loki endpoint

## API Endpoints

### Customers `/customers`
- `GET /customers/` - List all customers (requires `dashboard-customers-read`)
- `GET /customers/{id}` - Get customer by ID (requires `dashboard-customers-read`)
- `GET /customers/count` - Get customer count (requires `dashboard-customers-read`)

### Invoices `/invoices`
- `GET /invoices/` - List all invoices (requires `dashboard-invoices-read`)
- `GET /invoices/{id}` - Get invoice by ID (requires `dashboard-invoices-read`)
- `GET /invoices/latest` - Get latest invoices with optional indexFrom/indexTo params (requires `dashboard-invoices-read`)
- `GET /invoices/count` - Get invoice count, optional status filter (requires `dashboard-invoices-read`)
- `GET /invoices/amount` - Get total invoice amount, optional status filter (requires `dashboard-invoices-read`)
- `GET /invoices/pages` - Get page count for search (requires `dashboard-invoices-read`)
- `POST /invoices/search` - Search invoices with pagination (requires `dashboard-invoices-read`)
- `POST /invoices` - Create invoice (requires `dashboard-invoices-create`)
- `PUT /invoices/{id}` - Update invoice (requires `dashboard-invoices-update`)
- `DELETE /invoices/{id}` - Soft delete invoice (requires `dashboard-invoices-delete`)

### Users `/users`
- `GET /users/` - List all users (requires `dashboard-users-read`)
- `GET /users/{id}` - Get user by ID (requires `dashboard-users-read`)
- `GET /users/email/{email}` - Get user by email (requires `dashboard-users-read`)
- `POST /users/search` - Search users with pagination (requires `dashboard-users-create`)

### Revenues `/revenues`
- `GET /revenues/` - List all revenues (requires `dashboard-revenue-read`)

## Patterns to Follow

- All services have interfaces (ICustomerService, IInvoiceService, etc.)
- Constructor injection via Lombok's `@RequiredArgsConstructor`
- Map entities to DTOs through mapper interfaces
- DTOs follow naming: `{Entity}Create`, `{Entity}Read`, `{Entity}Update`
- Global exception handling via GlobalExceptionHandler with ProblemDetail responses
- Soft delete via `audit.deletedAt` field (set Instant, don't actually delete)
- Validate ObjectId format before parsing: `ObjectId.isValid(id)`
- Throw `ResourceNotFoundException` for 404 responses
- Use `@PreAuthorize` for endpoint authorization

## Testing

Tests organized by layer and entity in `src/test/java/com/dashboard/`:

```
controller/
├── customers/   # Integration tests (MockMvc)
├── invoices/
├── revenue/
└── users/
service/
├── customer/    # Unit tests (mocked repos)
├── invoice/
├── revenue/
└── user/
```

Each package has `Base*Test.java` with shared setup. Tests use:
- Embedded MongoDB (flapdoodle)
- DataFaker for test data
- Spring Security test utilities
- Allure annotations for reporting
