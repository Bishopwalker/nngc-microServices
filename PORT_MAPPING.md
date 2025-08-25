# NNGC MicroServices Port Mapping

## Current Port Assignments

| Service           | Port | URL                                    | Status |
|-------------------|------|----------------------------------------|---------|
| API Gateway       | 8080 | http://localhost:8080                 | 🔧 Ready to start |
| Customer Service  | 8081 | http://localhost:8081                 | 🔧 Ready to start |
| Token Service     | 8082 | http://localhost:8082                 | 🔧 Ready to start |
| Stripe Service    | 8083 | http://localhost:8083                 | 🔧 Ready to start |
| Email Service     | 8084 | http://localhost:8084                 | 🔧 Ready to start |
| Google Service    | 8085 | http://localhost:8085                 | 🔧 Ready to start |
| Service Registry  | 8761 | http://localhost:8761                 | 🔧 Ready to start |

## Service Dependencies (Start Order)
1. **Service Registry** (8761) - Service Discovery (Start First!)
2. **Customer Service** (8081) - Core Service
3. **Token Service** (8082) - Authentication Service
4. **Stripe Service** (8083) - Payment Service
5. **Email Service** (8084) - Notification Service
6. **Google Service** (8085) - Google Integration
7. **API Gateway** (8080) - Entry Point (Start Last!)

## Port Conflict Resolution ✅
- Fixed: API Gateway moved from 8081 → 8080
- All services now have unique ports assigned
- No conflicts with default Spring Boot ports