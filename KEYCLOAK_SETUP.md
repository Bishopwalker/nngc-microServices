# Keycloak Integration Setup for NNGC Microservices

## Overview
This setup integrates Keycloak as the identity and access management solution for the NNGC microservices architecture, providing secure OAuth2/OpenID Connect authentication and service-to-service communication.

## Architecture
- **API Gateway**: Entry point with JWT validation
- **Service Registry**: Secured Eureka server
- **Customer Service**: Protected microservice with secure WebClient
- **Token Service**: JWT token management with secure service calls
- **Email Service**: Secure WebClient for sending emails
- **Registration Service**: Secure WebClient for user registration
- **Google Service**: Secure WebClient for Google OAuth2 authentication
- **Stripe Service**: Secure WebClient for Stripe OAuth2 authentication

## Prerequisites
- Docker and Docker Compose
- Java 23+
- Maven 3.6+

## Quick Start

### 1. Start Keycloak
```bash
docker-compose -f docker-compose-keycloak.yml up -d
```

### 2. Access Keycloak Admin Console
- URL: http://localhost:8080
- Username: admin
- Password: admin

### 3. Configure Client Secrets
Update the following application configuration files with actual client secrets:

**API Gateway** (`api-gateway/src/main/resources/application.yml`):
```yaml
spring.security.oauth2.client.registration.keycloak.client-secret: api-gateway-secret
```

**Customer Service** (`customer-service/src/main/resources/application.yml`):
```yaml
spring.security.oauth2.client.registration.keycloak.client-secret: customer-service-secret
```

**Token Service** (`token-service/src/main/resources/application.yml`):
```yaml
spring.security.oauth2.client.registration.keycloak.client-secret: token-service-secret
```

**Service Registry** (`service-registry/src/main/resources/application.properties`):
```properties
spring.security.oauth2.client.registration.keycloak.client-secret=service-registry-secret
```

**Registration Service** (`registration-service/src/main/resources/application.properties`)
```properties
spring.security.oauth2.client.registration.keycloak.client-secret=registration-service-secret
```
**Google Service** (`google-service/src/main/resources/application.properties`)
```properties
spring.security.oauth2.client.registration.keycloak.client-secret=registration-service-secret
```
**Email Service**  (`email-service/src/main/resources/application.properties`)
```properties
spring.security.oauth2.client.registration.keycloak.client-secret=registration-service-secret
```
**Stripe Service**  (`stripe-service/src/main/resources/application.properties`)
```properties
spring.security.oauth2.client.registration.keycloak.client-secret=registration-service-secret
```


### 4. Start Services in Order
```bash
# 1. Service Registry
cd service-registry
mvn spring-boot:run

# 2. API Gateway
cd ../api-gateway
mvn spring-boot:run

# 3. Customer Service
cd ../customer-[.env](.env)service
mvn spring-boot:run

# 4. Token Service
cd ../token-service
mvn spring-boot:run

# 5. Registration Service
cd ../registration-service
mvn spring-boot:run

# 6. Google Service
cd ../google-service
mvn spring-boot:run

# 7. Email Service
cd ../email-service
mvn spring-boot:run

# 8. Stripe Service
cd ../stripe-service
mvn spring-boot:run

```

## Service Ports
- **Keycloak**: 8080
- **Customer Service**: 8081
- **Token Service**: 8083
- **Email Service**: 8084
- **Registration Service**: 8085
- **Stripe Service**: 8086
- **Google Service**: 8087
- **API Gateway**: 8088
- **Service Registry**: 8761

## Security Features

### JWT Authentication
- All services validate JWT tokens from Keycloak
- Service Registry, API Gateway, and microservices are secured
- Public endpoints: `/actuator/**`, `/eureka/**`

### Service-to-Service Communication
- WebClient configured with OAuth2 client credentials flow
- Automatic token acquisition and refresh
- Load-balanced service discovery through Eureka

### Client Credentials Flow
Each service has its own client configuration in Keycloak:
- `api-gateway`
- `customer-service` 
- `token-service`
- `service-registry`
- `registration-service`
- `email-service`
- `google-service`
- `stripe-service`

## Testing the Setup

### 1. Get Access Token
```bash
curl -X POST http://localhost:8080/realms/nngc-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&client_id=api-gateway&client_secret=api-gateway-secret"
```

### 2. Call Protected Endpoint
```bash
curl -H "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8081/api/customers/1
```

### 3. Verify Service Discovery
```bash
curl http://localhost:8761/eureka/apps
```

## Troubleshooting

### Common Issues
1. **Connection Refused**: Ensure Keycloak is running and accessible at localhost:8080
2. **Token Validation Failed**: Check JWT issuer URLs and Keycloak realm configuration
3. **Service Registration Failed**: Verify Eureka server is running and accessible

### Logs to Check
- Service startup logs for OAuth2 configuration
- WebClient connection logs
- JWT validation errors
- Eureka registration status

## Security Best Practices Implemented
- Client secrets stored in environment variables
- JWT token validation on all protected endpoints
- Service-to-service authentication using client credentials
- Proper CORS and CSRF configuration
- Secure WebClient configuration with automatic token management

## Next Steps
1. Configure environment-specific secrets
2. Set up SSL/TLS certificates
3. Implement role-based access control (RBAC)
4. Add monitoring and logging for security events
5. Configure token refresh strategies