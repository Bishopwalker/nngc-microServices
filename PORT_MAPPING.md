# NNGC MicroServices Port Mapping

## Current Port Assignments

| Service           | Port | URL                                    | Status |
|-------------------|------|----------------------------------------|---------|
| Keycloak          | 8080 | http://localhost:8080                 | 🐳 Docker |
| Customer Service  | 8081 | http://localhost:8081                 | 🔧 Ready to start |
| Token Service     | 8083 | http://localhost:8083                 | 🔧 Ready to start |
| Email Service     | 8084 | http://localhost:8084                 | 🔧 Ready to start |
| Registration Service | 8085 | http://localhost:8085              | 🔧 Ready to start |
| Stripe Service    | 8086 | http://localhost:8086                 | 🔧 Ready to start |
| Google Service    | 8087 | http://localhost:8087                 | 🔧 Ready to start |
| API Gateway       | 8088 | http://localhost:8088                 | 🔧 Ready to start |
| Service Registry  | 8761 | http://localhost:8761                 | 🔧 Ready to start |

## Service Dependencies (Start Order)
1. **Keycloak** (8080) - Authentication Server (Docker - Start First!)
2. **Service Registry** (8761) - Service Discovery 
3. **API Gateway** (8088) - Entry Point with Load Balancing
4. **Customer Service** (8081) - Core Service
5. **Token Service** (8083) - Authentication Service
6. **Email Service** (8084) - Notification Service
7. **Registration Service** (8085) - User Registration
8. **Stripe Service** (8086) - Payment Service
9. **Google Service** (8087) - Google Integration
