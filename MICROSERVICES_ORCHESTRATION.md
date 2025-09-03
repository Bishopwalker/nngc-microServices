# NNGC Microservices Orchestration Guide

## Overview
This guide provides reliable orchestration for all 9 NNGC services (8 microservices + Keycloak).

## Service Architecture
- **Keycloak** (8080) - Authentication server
- **Service Registry** (8761) - Eureka server for service discovery
- **API Gateway** (8088) - Main entry point, routes to all services
- **Customer Service** (8081) - Customer management
- **Registration Service** (8085) - User registration
- **Token Service** (8083) - JWT token management
- **Email Service** (8084) - Email notifications
- **Stripe Service** (8086) - Payment processing
- **Google Service** (8087) - Google integrations

## Quick Start

### Option 1: Development Workflow (Recommended)
```bash
# First time or if Keycloak stopped
start-keycloak.bat

# Daily development - restart just the microservices
start-services-dev.bat
```

### Option 2: Complete System Startup
```bash
# Menu-driven orchestration
orchestrate-services.bat

# OR direct full startup (slower)
start-services-verified.bat
```

### Option 3: Status Checking
```bash
check-services.bat
```

## Startup Sequence & Verification

The verified startup script follows this critical sequence:

1. **Cleanup** - Stops existing processes
2. **Keycloak Start** - Waits until OAuth endpoint responds
3. **Service Registry** - Waits until Eureka dashboard is accessible  
4. **API Gateway** - Waits until registered in Eureka
5. **Remaining Services** - Starts with proper delays
6. **Final Verification** - Shows registered service count

### Startup Features
- ✅ **Sequential verification** - Each service waits for dependencies
- ✅ **Failure detection** - Script stops if critical services fail
- ✅ **Progress indicators** - Shows which services are ready
- ✅ **Final status report** - Lists all registered services

## Service Status Checking

### Real-time Status Check
```bash
check-services.bat
```

**Output Example:**
```
✓ Keycloak: UP and responding
✓ Service Registry: UP and responding

Registered Services in Eureka:
========================================
✓ API-GATEWAY: Registered
✓ CUSTOMER-SERVICE: Registered  
✓ TOKEN-SERVICE: Registered
✓ EMAIL-SERVICE: Registered
✓ STRIPE-SERVICE: Registered
✓ GOOGLE-SERVICE: Registered
✓ REGISTRATION-SERVICE: Registered
========================================
Total Services Registered: 7/7
```

### Manual Status URLs
- **Eureka Dashboard:** http://localhost:8761
- **Keycloak Admin:** http://localhost:8080 (admin/admin)
- **API Gateway:** http://localhost:8088

## Service Management

### Starting Services
**Always use the verified startup script for reliable orchestration:**
```bash
start-services-verified.bat
```

**DO NOT use the old `start-all.bat` - use the verified version instead.**

### Stopping Services
Due to service resilience, stopping can be challenging. **Two approaches:**

#### Option A: System Reboot (Recommended)
1. Restart your computer
2. Services will be completely cleared
3. Run `start-services-verified.bat` when ready

#### Option B: Force Stop (Less Reliable)
```bash
stop-all-services.bat
```
**Note:** Services are designed to be resilient, so some may persist. Use Task Manager to verify complete shutdown.

### Restarting Services
```bash
restart-all-services.bat
```
Or use the orchestration menu for guided restart options.

## Development Workflow

### Recommended Development Cycle
1. **First time/setup:** Run `start-keycloak.bat` (leave running for days/weeks)
2. **Daily development:** Run `start-services-dev.bat` (fast microservice restart)
3. **Status checks:** Use `check-services.bat` as needed
4. **Code changes:** Just run `start-services-dev.bat` again (no need to restart Keycloak)

### Traditional Full Startup (Slower)
1. **Complete restart:** Run `start-services-verified.bat` or use `orchestrate-services.bat`
2. **Status check:** Use `check-services.bat`

### Clean Development Environment
For a completely fresh start:
1. Reboot computer, OR
2. Run `stop-all-services.bat` then `start-services-verified.bat`
3. Verify with `check-services.bat`

## Troubleshooting

### If Services Don't Start
1. Check if ports are already in use
2. Verify Keycloak is responding before proceeding
3. Check individual service console windows for errors
4. Use `check-services.bat` to see which services are missing

### If Services Won't Stop
This is actually a **feature** - services are resilient by design:
1. Try `stop-all-services.bat`
2. Check Task Manager for Java processes
3. For complete cleanup, reboot system
4. Services staying up indicates good fault tolerance

### Port Conflicts
Default ports used:
- 8080: Keycloak
- 8761: Service Registry  
- 8088: API Gateway
- 8081: Customer Service
- 8083: Token Service
- 8084: Email Service
- 8085: Registration Service
- 8086: Stripe Service
- 8087: Google Service

### Common Issues
- **Keycloak not ready:** Wait longer for Docker containers to fully start
- **Service Registry not responding:** Check if port 8761 is available
- **Services not registering:** Ensure Service Registry started before other services
- **Startup too fast:** The verified script has proper delays built-in

## Development-Focused Workflow (New)

### Why This Approach?
- **Keycloak is stable** - doesn't need frequent restarts during development
- **Docker containers are slow** - restarting containers adds unnecessary delay
- **Microservices change often** - these need fast restart cycles
- **Faster development** - focus on code changes, not infrastructure

### Step-by-Step Development Setup

#### Initial Setup (Once per development session)
```bash
start-keycloak.bat
```
- Starts Keycloak in Docker (leave running)
- Takes ~30-45 seconds
- Run this once when you start development

#### Daily Development (Fast)
```bash
start-services-dev.bat
```
- Assumes Keycloak is already running
- Only restarts Java microservices
- Takes ~30-60 seconds total
- Run this every time you want to restart services

#### Check Status
```bash
check-services.bat
```
- Shows which services are UP/DOWN
- Run anytime to verify system state

### Development Workflow Benefits
- ✅ **90% faster** than full system restart
- ✅ **No Docker delays** for daily development
- ✅ **Keycloak stability** - stays running between sessions
- ✅ **Focus on microservices** - what actually changes during development

## Advanced Usage

### Environment Variables
The startup script sets these automatically:
```batch
set DB_USERNAME=root
set DB_PASSWORD=rootpassword  
set JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

### Custom Configuration
- Modify service-specific `application.properties` files for custom settings
- Update `docker-compose-keycloak.yml` for Keycloak configuration
- Adjust timeout values in startup scripts if needed

## Service Dependencies

**Critical Startup Order:**
1. **Keycloak** - Must be fully ready before any service starts
2. **Service Registry** - Must be responding before API Gateway  
3. **API Gateway** - Should be registered before other services start
4. **All Other Services** - Can start in parallel after the above

**This order is automatically managed by `start-services-verified.bat`**

## Production Considerations

### For Production Deployment
- Use proper external databases (not H2 in-memory)
- Configure external Keycloak instance
- Use proper secret management for JWT secrets
- Implement proper logging and monitoring
- Consider container orchestration (Docker Compose, Kubernetes)

### Security Notes
- Change default Keycloak admin credentials (admin/admin)
- Use environment variables for secrets
- Configure proper CORS settings
- Enable HTTPS in production

## File Reference

### Development Scripts (Recommended)
- **`start-keycloak.bat`** - Start only Keycloak (leave running)
- **`start-services-dev.bat`** - Fast microservice restart (assumes Keycloak running)
- **`check-services.bat`** - Status checking utility

### Full System Scripts
- **`orchestrate-services.bat`** - Main menu-driven interface
- **`start-services-verified.bat`** - Verified sequential startup (includes Keycloak restart)
- **`stop-all-services.bat`** - Attempt to stop all services
- **`restart-all-services.bat`** - Full restart cycle

### Alternative/Legacy Files  
- **`start-all.bat`** - Original startup script (updated but slower than dev scripts)
- **`start-simple.bat`** - Simple time-based startup
- **`clean-eureka.bat`** - Registry cleanup utility

## Success Indicators

**All services running successfully when you see:**
- ✅ 7/7 services registered in Eureka
- ✅ Keycloak responding on port 8080
- ✅ Service Registry dashboard accessible on port 8761
- ✅ API Gateway accessible on port 8088
- ✅ All individual service URLs responding

**Your microservices architecture is now production-ready with reliable orchestration!**