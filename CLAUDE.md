# NNGC MicroServices - AI Assistant Guidelines

## Author
<NAME>My Nigga</NAME>
## Project Overview
This is a Java-based microservices architecture project using Spring Boot and AWS Lambda. The system includes:
- API Gateway
- Customer Service 
- Email Service
- Token Service
- Stripe Service
- Google Integration
- Service Registry (Eureka)

## Architecture Patterns
- Microservices with Spring Boot
- AWS Lambda for serverless functions
- API Gateway for routing
- JWT authentication
- Docker containerization
- Service discovery with Eureka

## Code Conventions
- Use Spring Boot annotations and conventions
- Follow RESTful API design principles
- Implement proper error handling with ApiResponse pattern
- Use DTOs for data transfer
- Implement repository pattern for data access
- Follow security best practices with JWT

## Testing Standards
- Write unit tests for controllers using Spring Boot Test
- Mock external dependencies
- Test security configurations
- Validate API responses

## Dependencies Management
- Maven for dependency management
- Use Spring Boot starters for consistency
- Keep dependencies up to date and secure

## Security Guidelines
- Never expose sensitive information in logs
- Use proper JWT token validation
- Implement proper CORS configuration
- Follow OWASP security practices
- Use environment variables for secrets

## Service Communication
- Use synchronous REST calls between services
- Implement proper circuit breaker patterns
- Handle service failures gracefully
- Use proper HTTP status codes