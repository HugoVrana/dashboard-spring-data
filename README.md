# Dashboard Application API

A Spring Boot REST API backend for a dashboard application that simulates a company called Acme, tracking their invoices to customers. This is a learning project for the Spring + Next.js stack.

## Tech Stack

- **Java 21**
- **Spring Boot 3.4.3**
- **MongoDB** - Document database
- **Spring Security** - JWT authentication
- **OpenAPI/Swagger** - API documentation
- **Allure** - Test reporting
- **Lombok** - Boilerplate reduction
- **Caffeine** - Caching

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MongoDB (or use embedded MongoDB for testing)

### Build and Run

```bash
# Build the project
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### API Documentation

When running locally, OpenAPI documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Project Structure

```
src/main/java/com/dashboard/
├── controller/          # REST endpoints
├── service/             # Business logic
├── repository/          # Data access (MongoDB)
├── model/entities/      # Domain entities
├── dataTransferObject/  # Request/Response DTOs
├── mapper/              # Entity-DTO mappers
├── config/              # Configuration
├── filter/              # Security filters
└── exceptionhandlers/   # Error handling
```

## Frontend

The frontend is a Next.js app with Tailwind CSS hosted on [Vercel](https://nextjs-dashboard-two-beryl.vercel.app/).

Repository: [nextjs-dashboard](https://gitlab.com/hugo.vrana/nextjs-dashboard)

## Deployment

This API is hosted on [Render](https://spring-dashboard-1.onrender.com).

Repository: [spring-dashboard](https://gitlab.com/hugo.vrana/spring-dashboard)

## License

See [LICENSE](LICENSE) file for details.