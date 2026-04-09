# Dashboard Application API

A Spring Boot REST API backend for a dashboard application that simulates a company called Acme, tracking their invoices
to customers. This is a learning project for the Spring + Next.js stack.

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

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## API Endpoints

All endpoints require JWT authentication with appropriate grants.

### Customers `/api/v1/customers`

| Method | Endpoint | Description | Required Grant |
|--------|----------|-------------|----------------|
| GET | `/api/v1/customers/` | List all customers | `dashboard-customers-read` |
| GET | `/api/v1/customers/{id}` | Get customer by ID | `dashboard-customers-read` |
| GET | `/api/v1/customers/count` | Get customer count | `dashboard-customers-read` |
| POST | `/api/v1/customers` | Create customer | `dashboard-customers-create` |
| POST | `/api/v1/customers/{id}/image` | Upload customer image (multipart/form-data) | `dashboard-customers-update` |
| PUT | `/api/v1/customers/{id}` | Update customer | `dashboard-customers-update` |
| DELETE | `/api/v1/customers/{id}` | Soft delete customer | `dashboard-customers-delete` |

### Invoices `/api/v1/invoices`

| Method | Endpoint | Description | Required Grant |
|--------|----------|-------------|----------------|
| GET | `/api/v1/invoices/` | List all invoices | `dashboard-invoices-read` |
| GET | `/api/v1/invoices/{id}` | Get invoice by ID | `dashboard-invoices-read` |
| GET | `/api/v1/invoices/latest` | Get latest invoices (optional `indexFrom`/`indexTo` params) | `dashboard-invoices-read` |
| GET | `/api/v1/invoices/count` | Get invoice count (optional `status` filter) | `dashboard-invoices-read` |
| GET | `/api/v1/invoices/amount` | Get total invoice amount (optional `status` filter) | `dashboard-invoices-read` |
| GET | `/api/v1/invoices/pages` | Get page count for search (optional `searchTerm`/`size` params) | `dashboard-invoices-read` |
| POST | `/api/v1/invoices/search` | Search invoices with pagination | `dashboard-invoices-read` |
| POST | `/api/v1/invoices` | Create invoice | `dashboard-invoices-create` |
| PUT | `/api/v1/invoices/{id}` | Update invoice | `dashboard-invoices-update` |
| DELETE | `/api/v1/invoices/{id}` | Soft delete invoice | `dashboard-invoices-delete` |

### Revenues `/api/v1/revenues`

| Method | Endpoint | Description | Required Grant |
|--------|----------|-------------|----------------|
| GET | `/api/v1/revenues/` | List all revenues | `dashboard-revenue-read` |

### Activity `/api/v1/activity`

| Method | Endpoint | Description | Required Grant |
|--------|----------|-------------|----------------|
| GET | `/api/v1/activity/recent` | Get recent activity events (optional `limit` param, default 50) | none |

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