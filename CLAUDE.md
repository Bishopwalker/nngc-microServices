# NNGC MicroServices - AI Assistant Guidelines

## Author
<NAME>My Nigga</NAME>
Always Refer to me by Name. 
## Project Overview
This is a Java-based microservices architecture project using Spring Boot And Docker Containers. The system includes:
- API Gateway
- Customer Service 
- Email Service
- Token Service
- Stripe Service
- Google Integration
- Service Registry (Eureka)
- KeyCloak 

## Always Ensure
- File Structure is clean with no unneeded files
- Recheck all good to ensure nothing is added that is extra
- Always check how updates to code can affect functionality
- Remove unused files from git 
- Code is clean and readable
- Code is well-documented
- Code is well-tested
- Code is well-architected
- Code is secure
- Code is easy to maintain
- Code is easy to extend

## Architecture Patterns
- Microservices with Spring Boot
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
- Use env Files

## Service Communication
- Use synchronous REST calls between services
- Implement proper circuit breaker patterns
- Handle service failures gracefully
- Use proper HTTP status codes

## Cleanup
- Never Leave extra Files or Code that is unused
- Remove unused dependencies
