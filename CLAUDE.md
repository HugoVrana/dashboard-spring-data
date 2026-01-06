# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run Commands

```bash
# Build the application
./mvnw clean package

# Build without tests
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ClassName
```

## Architecture Overview

This is a Spring Boot 3.4.3 REST API (Java 21) for a dashboard system managing users, invoices, customers, and revenues. Data is stored in MongoDB.

### Package Structure (com.dashboard)

- **controller/** - REST endpoints with @PreAuthorize for authorization
- **service/** - Business logic with interface-based design (IUserService, etc.)
- **repository/** - MongoDB repositories extending MongoRepository
- **model/entities/** - MongoDB documents with @Document annotation
- **dataTransferObject/** - DTOs split by operation (Read/Create/Update patterns)
- **mapper/** - Entity-to-DTO converters with interface contracts
- **filter/** - JWT validation (JwtGrantsFilter) and request caching
- **interceptor/** - API logging for all requests
- **config/** - Security, CORS, and web configuration
- **authentication/** - Custom GrantsAuthentication for JWT claims

### Security Model

JWT tokens contain a "grants" claim (List<String>) for authorization. Controllers use @PreAuthorize to check grants like "dashboard-users-read" or "dashboard-invoices-create". The JwtGrantsFilter extracts and validates tokens using HS256 with the JWT.SECRET environment variable.

### Data Patterns

- Entities inherit an Audit object for soft delete (deletedAt field)
- @DBRef for object references (e.g., Invoice references Customer)
- Repositories filter by audit_DeletedAtIsNull for active records
- Pagination via Spring Data Pageable with custom PageRead DTO

### Request Flow

1. JwtGrantsFilter validates JWT and populates SecurityContext
2. RequestResponseCachingFilter wraps request/response for logging
3. ApiLoggingInterceptor logs request details to Grafana
4. Controller handles request with @PreAuthorize checks
5. GlobalExceptionHandler catches and formats errors

## Required Environment Variables

```properties
spring.data.mongodb.uri=mongodb+srv://<user>:<pass>@<cluster>.mongodb.net/dashboard
JWT.SECRET=<base64-encoded-secret>
grafana.apiKey=<api-key>  # optional, for logging
grafana.url=<grafana-loki-url>  # optional
```

## API Documentation

SpringDoc OpenAPI is configured. Access Swagger UI at `/swagger-ui.html` when running locally.
