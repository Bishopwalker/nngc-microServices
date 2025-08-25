# NNGC MicroServices Port Mapping

## Current Port Assignments

| Service           | Port | URL                                    | Status |
|-------------------|------|----------------------------------------|---------|
| Keycloak          | 8080 | http://localhost:8080                 | ✅ Running |
| API Gateway       | 8081 | http://localhost:8081                 | 🔧 Ready to start |
| Customer Service  | 8082 | http://localhost:8082                 | 🔧 Ready to start |
| Token Service     | 8083 | http://localhost:8083                 | 🔧 Ready to start |
| Service Registry  | 8761 | http://localhost:8761                 | ✅ Running |
| Keycloak DB       | 5433 | postgresql://localhost:5433           | ✅ Running |

## Service Dependencies (Start Order)
1. **Keycloak** (8080) - Identity Provider
2. **Service Registry** (8761) - Service Discovery  
3. **Customer Service** (8082) - Core Service
4. **Token Service** (8083) - Authentication Service
5. **API Gateway** (8081) - Entry Point

## No Port Conflicts! ✅
All services have unique ports assigned.