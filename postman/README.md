# Postman Setup for NNGC Microservices

## Quick Setup Instructions

### 1. Import Files into Postman
1. Open Postman
2. Click "Import" button
3. Import both files:
   - `NNGC-Collection.postman_collection.json` - The complete API collection
   - `NNGC-Environment.postman_environment.json` - All environment variables

### 2. Select Environment
1. In Postman, click the environment dropdown (top right)
2. Select "NNGC Development"

### 3. Start Required Services
Make sure these services are running:
```bash
# Start Keycloak
./start-keycloak.bat

# Start all microservices
./start-services-dev.bat
```

### 4. Test Authentication
1. Go to "Authentication" folder
2. Run "Get Access Token" request
3. Token will be automatically saved to environment

## Features

### Auto Token Refresh
The collection includes a pre-request script that automatically:
- Checks if token exists
- Refreshes token if needed
- Applies token to all protected requests

### Environment Variables
All variables are pre-configured:
- Base URLs for all services
- Keycloak configuration
- Test user credentials
- Service-specific endpoints

### Available Services
- **Registration Service** - User registration and confirmation
- **Customer Service** - CRUD operations for customers
- **Email Service** - Email sending functionality
- **Stripe Service** - Payment processing
- **Google Service** - Google OAuth integration
- **Token Service** - Authentication endpoints
- **Actuator** - Health checks and metrics

## Testing Workflow

### 1. Public Endpoints (No Auth)
Test these first:
- `Registration Service > Health Check`
- `Actuator Endpoints > Health Check`

### 2. Get Authentication Token
- Run `Authentication > Get Access Token`
- Token is automatically saved

### 3. Protected Endpoints
Now you can test all protected endpoints:
- `Customer Service > Get All Customers`
- `Email Service > Send Email`
- `Stripe Service > Create Payment`

## Troubleshooting

### Token Issues
- If getting 401 errors, run "Get Access Token" again
- Check Keycloak is running: http://localhost:8080
- Verify realm name is correct: `nngc-realm`

### Service Connection Issues
- Verify API Gateway is running: http://localhost:8088
- Check service registry: http://localhost:8761
- Ensure all microservices are registered

### Quick Health Check
Run these to verify setup:
1. `Actuator Endpoints > Health Check` - Should return UP
2. `Actuator Endpoints > Gateway Routes` - Shows all registered routes

## Variables Reference

| Variable | Default Value | Description |
|----------|---------------|-------------|
| base_url | http://localhost:8088 | API Gateway URL |
| keycloak_url | http://localhost:8080 | Keycloak server |
| realm | nngc-realm | Keycloak realm |
| client_id | api-gateway | OAuth client ID |
| client_secret | api-gateway-secret | OAuth client secret |
| access_token | (auto-generated) | JWT token |

## Tips
- Collection auth is set at collection level - all requests inherit it
- Pre-request script runs before each request for token management
- Test scripts automatically save tokens to environment
- All service URLs use variables for easy environment switching