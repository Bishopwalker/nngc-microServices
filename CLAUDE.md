# CLAUDE.md - NNGC Microservices Project Guidelines

This file provides comprehensive guidance for working with the NNGC Microservices architecture.

## Project Overview

NNGC Microservices is a Spring Boot-based microservices architecture with the following core services:
- **API Gateway**: Entry point for all requests, handles routing and security
- **Service Registry (Eureka)**: Service discovery and registration
- **Customer Service**: Customer management and operations
- **Registration Service**: User registration and onboarding
- **Token Service**: JWT token generation and validation  
- **Email Service**: Email notifications and communications
- **Google Service**: Google OAuth integration
- **Stripe Service**: Payment processing via Stripe

## Tech Stack

- **Java 23** with Eclipse Temurin JDK
- **Spring Boot 3.3.5**
- **Spring Cloud** for microservices patterns
- **PostgreSQL** for database
- **Docker & Docker Compose** for containerization
- **Keycloak** for identity and access management
- **RabbitMQ** for message queuing
- **Prometheus & Grafana** for monitoring

## Development Principles

### Code Style & Standards

- **Always follow Java best practices** and Spring Boot conventions
- **Use proper exception handling** - catch specific exceptions, not generic Exception
- **Avoid wildcard imports** - use explicit imports
- **Extract magic strings** to constants
- **Use dependency injection** via constructor injection (not field injection)
- **Follow RESTful API conventions** for endpoints

### Security First

- **Never commit secrets** - use environment variables or application properties
- **Always validate input** - use Bean Validation annotations
- **Implement proper CORS configuration** in API Gateway
- **Use Keycloak** for authentication and authorization
- **Secure inter-service communication** with proper authentication

### Testing Requirements

- **Write unit tests** for all business logic
- **Use Mockito** for mocking dependencies
- **Test coverage** should be at least 80% for critical paths
- **Integration tests** for REST endpoints using MockMvc
- **Use @TestPropertySource** or test profiles for test configuration

## Project Structure

```
NNGC MicroServices/
├── api-gateway/                 # API Gateway service
├── customer-service/            # Customer management
├── registration-service/        # User registration
├── token-service/              # JWT token handling
├── email-service/              # Email notifications
├── google-service/             # Google OAuth
├── stripe-service/             # Payment processing
├── service-registry/           # Eureka server
├── monitoring/                 # Prometheus & Grafana configs
├── postman/                    # API test collections
├── scripts/                    # Utility scripts
├── docker-compose.yml          # Production compose
├── docker-compose.dev.yml      # Development compose
└── control.bat                 # Windows control script
```

## Service Communication

### Service Registry (Eureka)
All services register with Eureka for service discovery:
- Default port: 8761
- Services use service names for inter-service communication
- Example: `http://CUSTOMER-SERVICE/api/customers`

### REST Clients
Use Spring Cloud OpenFeign for declarative REST clients:
```java
@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomer(@PathVariable Long id);
}
```

## Docker & Deployment

### Local Development
```bash
# Start all services
docker-compose -f docker-compose.dev.yml up

# Start specific service
docker-compose -f docker-compose.dev.yml up customer-service

# Rebuild and start
docker-compose -f docker-compose.dev.yml up --build
```

### Environment Configuration
Each service has:
- `application.properties` - default configuration
- `application-docker.properties` - Docker environment config
- `application-dev.properties` - development config
- `application-prod.properties` - production config

## Database Standards

### Entity Naming
- Tables: plural lowercase (e.g., `customers`, `orders`)
- Primary keys: `id` (Long, auto-generated)
- Foreign keys: `{entity}_id` (e.g., `customer_id`)
- Timestamps: `created_at`, `updated_at`

### JPA Best Practices
- Use `@Entity` and `@Table` annotations
- Define relationships with proper cascade types
- Use `@Repository` for data access layer
- Implement soft deletes where appropriate

## API Design Standards

### RESTful Endpoints
```
GET    /api/customers           # List all
GET    /api/customers/{id}      # Get one
POST   /api/customers           # Create
PUT    /api/customers/{id}      # Update
DELETE /api/customers/{id}      # Delete
```

### Response Format
```json
{
  "status": "success|error",
  "message": "Operation message",
  "data": { },
  "timestamp": "2024-08-30T10:00:00Z"
}
```

### Error Handling
Use proper HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

## Build & Run Commands

### Maven Commands
```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Skip tests during build
mvn clean package -DskipTests

# Run specific service
mvn spring-boot:run

# Package as Docker image
mvn spring-boot:build-image
```

### Control Scripts
```bash
# Windows
control.bat start all       # Start all services
control.bat stop all        # Stop all services
control.bat restart all     # Restart all services
control.bat logs customer   # View logs for customer service
```

## Git Workflow

### Branch Strategy
- `main` - production-ready code
- `bishop_dev` - development branch
- `feature/*` - new features
- `fix/*` - bug fixes

### Commit Messages
Format: `<type>: <description>`

Types:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `style:` Code style changes
- `refactor:` Code refactoring
- `test:` Test updates
- `chore:` Build/config updates

## Monitoring & Logging

### Logging Standards
- Use SLF4J with Logback
- Log levels: ERROR, WARN, INFO, DEBUG, TRACE
- Include correlation IDs for request tracing
- Structure: `[SERVICE_NAME] [LEVEL] [CORRELATION_ID] Message`

### Metrics & Monitoring
- Prometheus endpoint: `/actuator/prometheus`
- Health check: `/actuator/health`
- Grafana dashboards for visualization
- Alert on critical metrics

## Common Tasks

### Adding a New Service
1. Create Spring Boot project with required dependencies
2. Configure `application.properties` with Eureka registration
3. Add Docker configuration
4. Update docker-compose files
5. Add Feign client interfaces in dependent services
6. Write tests
7. Update documentation

### Debugging Issues
1. Check service registry (Eureka) - all services registered?
2. Verify network connectivity between services
3. Check logs for exceptions
4. Verify configuration properties
5. Test endpoints with Postman
6. Check database connections

## Important Notes

- **Always run tests** before committing: `mvn test`
- **Update Postman collections** when adding/modifying endpoints
- **Document API changes** in service README files
- **Use environment variables** for sensitive configuration
- **Follow security best practices** - never expose internal services directly
- **Monitor service health** regularly
- **Keep dependencies updated** but test thoroughly

## Troubleshooting

### Service Won't Start
- Check if ports are already in use
- Verify database connections
- Check Eureka registration
- Review application logs

### Inter-Service Communication Fails
- Verify service names in Feign clients
- Check if services are registered in Eureka
- Verify network configuration in Docker
- Check security/authentication between services

### Database Issues
- Verify connection strings
- Check credentials
- Ensure database is running
- Review JPA/Hibernate logs

---

_This document should be updated as the project evolves and new patterns emerge._