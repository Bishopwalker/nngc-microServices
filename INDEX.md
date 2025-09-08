# NNGC MicroServices - Navigation Index

This document provides a comprehensive navigation guide for the NNGC Spring Boot microservices project, helping developers quickly locate relevant code, documentation, and resources.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │────│ Service Registry│────│   PostgreSQL    │
│   Port: 8080    │    │   Port: 8761    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │
    ┌────┴────────────────────────────────────────┐
    │                                             │
┌───▼────┐ ┌────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│Customer│ │Register│ │  Token   │ │  Email   │ │  Stripe  │
│ :8081  │ │ :8082  │ │  :8083   │ │  :8084   │ │  :8085   │
└────────┘ └────────┘ └──────────┘ └──────────┘ └──────────┘
```

## 📁 Project Structure Guide

### Core Services
| Service | Port | Purpose | Key Files |
|---------|------|---------|-----------|
| **api-gateway** | 8080 | Routing, Security, CORS | `GatewayConfig.java`, `SecurityConfig.java` |
| **customer-service** | 8081 | Customer Management | `CustomerController.java`, `CustomerService.java` |
| **registration-service** | 8082 | User Registration | `RegistrationController.java`, `RegistrationService.java` |
| **token-service** | 8083 | JWT Authentication | `TokenController.java`, `JwtUtil.java` |
| **email-service** | 8084 | Email Notifications | `EmailController.java`, `EmailService.java` |
| **google-service** | 8087 | OAuth Integration | `GoogleOAuthService.java` |
| **stripe-service** | 8085 | Payment Processing | `StripeController.java`, `StripeService.java` |
| **service-registry** | 8761 | Service Discovery | `EurekaServerApplication.java` |

### Configuration Files
| File | Purpose | Location |
|------|---------|----------|
| `docker-compose.yml` | Production deployment | Root |
| `docker-compose.dev.yml` | Development environment | Root |
| `application.properties` | Service configuration | Each service `/src/main/resources/` |
| `application-docker.properties` | Docker-specific config | Each service `/src/main/resources/` |
| `pom.xml` | Maven dependencies | Root + Each service |

### Infrastructure
| Component | Purpose | Files |
|-----------|---------|--------|
| **Docker** | Containerization | `Dockerfile` in each service |
| **PostgreSQL** | Database | `docker-compose*.yml` |
| **Keycloak** | Identity Management | `keycloak/` directory |
| **Monitoring** | Observability | `monitoring/` directory |
| **CI/CD** | Automation | `.gitlab-ci.yml` |

## 🔍 Quick Navigation

### Finding Code Patterns
```bash
# Find REST controllers
find . -name "*Controller.java" -type f

# Find service classes  
find . -name "*Service.java" -type f

# Find configuration classes
find . -name "*Config.java" -type f

# Find tests
find . -name "*Test.java" -type f
```

### Common Code Locations
| What You're Looking For | Where to Find It |
|------------------------|------------------|
| **REST Endpoints** | `*/src/main/java/*/controller/*Controller.java` |
| **Business Logic** | `*/src/main/java/*/service/*Service.java` |
| **Data Access** | `*/src/main/java/*/repository/*Repository.java` |
| **DTOs/Models** | `*/src/main/java/*/dto/*DTO.java` |
| **Configuration** | `*/src/main/java/*/config/*Config.java` |
| **Tests** | `*/src/test/java/**/*Test.java` |

### Configuration Patterns
```java
// Service Registration Pattern
@EnableEurekaClient
@SpringBootApplication
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}

// REST Controller Pattern  
@RestController
@RequestMapping("/api/v1/{resource}")
@Validated
public class ResourceController {
    // Standard CRUD operations
}

// Service Layer Pattern
@Service
@Transactional
public class ResourceService {
    // Business logic implementation
}
```

## 📚 Documentation Map

### Primary Documentation
| Document | Purpose | Audience |
|----------|---------|----------|
| [`README.md`](README.md) | Project overview and setup | All developers |
| [`CLAUDE.md`](CLAUDE.md) | AI development guidelines | AI assistants |
| [`DEV_WORKFLOW.md`](DEV_WORKFLOW.md) | Development processes | Developers |
| [`KEYCLOAK_SETUP.md`](KEYCLOAK_SETUP.md) | Identity setup | DevOps |
| [`PORT_MAPPING.md`](PORT_MAPPING.md) | Service ports | All |

### Service-Specific Docs
| Service | Documentation Location |
|---------|----------------------|
| API Gateway | `api-gateway/README.md` |
| Customer Service | `customer-service/README.md` |
| Registration Service | `registration-service/README.md` |
| Token Service | `token-service/README.md` |
| Email Service | `email-service/README.md` |

### Operations Documentation  
| Topic | File | Purpose |
|-------|------|---------|
| **Deployment** | `docker-compose*.yml` | Container orchestration |
| **Monitoring** | `monitoring/README.md` | Observability setup |
| **Control Scripts** | `control.bat` | Windows service management |
| **Environment Setup** | `.env.example` | Configuration template |

## 🛠️ Development Workflows

### Starting Development
1. **Clone & Setup**: Follow [`README.md`](README.md)
2. **Environment**: Copy `.env.example` to `.env`
3. **Services**: Start with `docker-compose -f docker-compose.dev.yml up`
4. **Verify**: Check Eureka at http://localhost:8761

### Adding New Features
1. **Branch**: `git checkout -b feature/service-name-feature`
2. **Code**: Follow patterns in existing services
3. **Test**: Write unit and integration tests
4. **Validate**: Run `mvn test` before commit
5. **Document**: Update relevant documentation

### Common Tasks
```bash
# Start all services
docker-compose -f docker-compose.dev.yml up

# Start specific service
docker-compose -f docker-compose.dev.yml up customer-service

# View logs  
docker-compose logs customer-service -f

# Run tests
mvn test -pl customer-service

# Build service
mvn clean package -pl customer-service
```

## 🔧 Troubleshooting Quick Reference

### Service Won't Start
1. Check port availability: `netstat -an | findstr :8081`
2. Verify Docker: `docker ps`
3. Check logs: `docker-compose logs service-name`
4. Database connection: Verify PostgreSQL is running

### Service Registration Issues
1. Check Eureka: http://localhost:8761
2. Verify `application.properties` Eureka config
3. Network connectivity: `docker network ls`
4. Service naming consistency

### Database Problems
1. Connection string in `application-docker.properties`
2. PostgreSQL container: `docker-compose logs postgres`
3. Schema initialization scripts
4. Connection pool configuration

## 🧪 Testing Strategy

### Test Types
| Test Level | Location | Purpose | Tools |
|-----------|----------|---------|-------|
| **Unit** | `src/test/java/**` | Component logic | JUnit 5, Mockito |
| **Integration** | `src/test/java/**` | Service integration | MockMvc, TestContainers |
| **End-to-End** | `postman/` | Full workflows | Postman Collections |

### Running Tests
```bash
# All tests
mvn test

# Service-specific tests
mvn test -pl customer-service

# Integration tests only
mvn test -Dtest=*IntegrationTest

# With coverage
mvn test jacoco:report
```

## 🚀 Deployment Guide

### Local Development
- Use `docker-compose.dev.yml`
- Hot reload enabled
- Debug ports exposed

### Staging Environment
- Use `docker-compose.staging.yml`
- Production-like setup
- Monitoring enabled

### Production Deployment
- Use `docker-compose.prod.yml`  
- SSL termination
- Health checks
- Scaling configuration

## 📞 Getting Help

### Quick Reference
- **Service Registry**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Customer API**: http://localhost:8081/api/customers
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Common Commands
```bash
# System health check
curl http://localhost:8080/actuator/health

# Service discovery check
curl http://localhost:8761/eureka/apps

# Customer service health
curl http://localhost:8081/actuator/health
```

This index serves as your primary navigation tool for the NNGC microservices architecture. Bookmark this page and refer to it whenever you need to quickly locate code, documentation, or understand system workflows.

---
*Last updated: $(date)*