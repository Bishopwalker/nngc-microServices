# Postman Configuration for NNGC Microservices with Keycloak OAuth2

## Prerequisites
- Keycloak server running on `http://localhost:8080`
- API Gateway running on `http://localhost:8088`
- Postman installed

## Method 1: OAuth2 Client Credentials Flow (Recommended)

### Step 1: Create a New Postman Collection
1. Open Postman
2. Click "New" → "Collection"
3. Name it "NNGC Microservices"

### Step 2: Configure OAuth2 at Collection Level
1. Right-click on your collection → "Edit"
2. Go to the "Authorization" tab
3. Select Type: **OAuth 2.0**
4. Configure the following:

**Token Configuration:**
- Token Name: `NNGC Token`
- Grant Type: `Client Credentials`
- Access Token URL: `http://localhost:8080/realms/nngc-realm/protocol/openid-connect/token`
- Client ID: `api-gateway`
- Client Secret: `api-gateway-secret`
- Scope: `openid profile`
- Client Authentication: `Send as Basic Auth header`

5. Click "Get New Access Token"
6. Click "Use Token"

### Step 3: Create Environment Variables
1. Create a new Environment called "NNGC Dev"
2. Add these variables:
```
base_url: http://localhost:8088
keycloak_url: http://localhost:8080
realm: nngc-realm
client_id: api-gateway
client_secret: api-gateway-secret
```

## Method 2: Manual Token Request

### Step 1: Get Access Token
Create a new request:
- Method: `POST`
- URL: `http://localhost:8080/realms/nngc-realm/protocol/openid-connect/token`
- Body: `x-www-form-urlencoded`
```
grant_type: client_credentials
client_id: api-gateway
client_secret: api-gateway-secret
scope: openid profile
```

### Step 2: Use Token in Requests
Copy the `access_token` from the response and add to your API requests:
- Header: `Authorization`
- Value: `Bearer {access_token}`

## Testing Your Configuration

### Public Endpoints (No Auth Required)
These endpoints work without authentication:
```
GET http://localhost:8088/auth/nngc/health
GET http://localhost:8088/auth/nngc/registration
GET http://localhost:8088/actuator/health
```

### Protected Endpoints (Auth Required)
These require a valid JWT token:
```
GET http://localhost:8088/api/customers/all
GET http://localhost:8088/api/email/status
GET http://localhost:8088/api/payments/status
```

## Example Requests

### 1. Customer Service - Get All Customers
```
GET {{base_url}}/api/customers/all
Authorization: Bearer {{access_token}}
Content-Type: application/json
```

### 2. Registration Service - Register New User
```
POST {{base_url}}/auth/nngc/registration
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

### 3. Email Service - Send Email
```
POST {{base_url}}/api/email/send
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
  "to": "recipient@example.com",
  "subject": "Test Email",
  "body": "This is a test email"
}
```

## Postman Collection Setup Script

Add this Pre-request Script to your collection to automatically refresh tokens:

```javascript
const tokenUrl = pm.environment.get("keycloak_url") + "/realms/" + pm.environment.get("realm") + "/protocol/openid-connect/token";
const clientId = pm.environment.get("client_id");
const clientSecret = pm.environment.get("client_secret");

const getTokenRequest = {
    method: 'POST',
    url: tokenUrl,
    header: 'Content-Type:application/x-www-form-urlencoded',
    body: {
        mode: 'urlencoded',
        urlencoded: [
            { key: 'grant_type', value: 'client_credentials' },
            { key: 'client_id', value: clientId },
            { key: 'client_secret', value: clientSecret },
            { key: 'scope', value: 'openid profile' }
        ]
    }
};

pm.sendRequest(getTokenRequest, (err, response) => {
    if (err) {
        console.log(err);
    } else {
        const jsonResponse = response.json();
        pm.environment.set("access_token", jsonResponse.access_token);
        console.log("Token refreshed successfully");
    }
});
```

## Troubleshooting

### Common Issues:

1. **401 Unauthorized**
   - Check if Keycloak is running
   - Verify client credentials are correct
   - Ensure token hasn't expired

2. **Connection Refused**
   - Verify services are running on correct ports
   - Check if Keycloak is started: `http://localhost:8080`
   - Check if API Gateway is started: `http://localhost:8088`

3. **Invalid Token**
   - Token might be expired (default TTL is usually 5 minutes)
   - Click "Get New Access Token" in Postman
   - Check JWT issuer matches configuration

4. **CORS Issues**
   - These are handled by the API Gateway configuration
   - If testing from browser, ensure origin is allowed

## Keycloak Admin Console

Access Keycloak Admin Console to manage clients and users:
- URL: `http://localhost:8080/admin`
- Default admin credentials (if using default setup):
  - Username: `admin`
  - Password: `admin`

## Service Endpoints Reference

| Service | Base Path | Example Endpoint |
|---------|-----------|------------------|
| Customer Service | `/api/customers` | `/api/customers/all` |
| Registration Service | `/auth/nngc` | `/auth/nngc/registration` |
| Email Service | `/api/email` | `/api/email/send` |
| Stripe Service | `/api/payments` | `/api/payments/charge` |
| Google Service | `/api/google` | `/api/google/oauth` |
| Token Service | `/api/auth` | `/api/auth/login` |

## Notes
- Tokens expire after a certain period (usually 5 minutes by default)
- The API Gateway handles routing to individual microservices
- All protected routes require a valid JWT token in the Authorization header
- The `StripPrefix` filter removes the first segment of the path when routing