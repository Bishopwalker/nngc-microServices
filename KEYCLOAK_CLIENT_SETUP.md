# Keycloak Client Keys/Secrets Management

## Access Keycloak Admin Console

1. **Open Keycloak Admin Console**
   - URL: http://localhost:8080/admin
   - Username: `admin`
   - Password: `admin`

## View/Get Existing Client Secret

### For api-gateway client:
1. Login to Keycloak Admin Console
2. Select realm: `nngc-realm` (top left dropdown)
3. Navigate to: **Clients** → Click on `api-gateway`
4. Go to **Credentials** tab
5. You'll see:
   - Client Authenticator: `Client Id and Secret`
   - Secret: `api-gateway-secret` (or click "Regenerate" for new one)

## Create New Client with Credentials

### Step 1: Create Client
1. Go to **Clients** → **Create client**
2. Fill in:
   - Client type: `OpenID Connect`
   - Client ID: `your-service-name`
   - Name: `Your Service Display Name`
3. Click **Next**

### Step 2: Client Authentication
1. Enable **Client authentication** ✓
2. Enable **Service accounts roles** ✓
3. Authorization: Keep default
4. Click **Next**

### Step 3: Login Settings
1. Valid redirect URIs: `http://localhost:*` or specific URLs
2. Web origins: `+` (to allow CORS from redirect URIs)
3. Click **Save**

### Step 4: Get/Set Client Secret
1. After saving, go to **Credentials** tab
2. Copy the generated secret OR
3. Click **Regenerate** for a new secret

## Client Credential Types

### 1. Public Client (No Secret)
- For frontend applications
- Uses PKCE flow
- No client secret needed

### 2. Confidential Client (With Secret)
- For backend services
- Requires client secret
- Used for service-to-service auth

### 3. Bearer-only
- Only validates tokens
- Doesn't initiate login
- For resource servers

## Current Configured Clients

| Client ID | Secret | Type | Purpose |
|-----------|--------|------|---------|
| api-gateway | api-gateway-secret | Confidential | API Gateway authentication |
| customer-service | your-customer-service-secret | Confidential | Customer microservice |
| admin-cli | N/A | Public | Admin CLI access |

## Generate Client Credentials via REST API

### Get Admin Token
```bash
curl -X POST "http://localhost:8080/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  -d "client_id=admin-cli"
```

### Create New Client
```bash
curl -X POST "http://localhost:8080/admin/realms/nngc-realm/clients" \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "new-service",
    "enabled": true,
    "clientAuthenticatorType": "client-secret",
    "secret": "your-new-secret",
    "serviceAccountsEnabled": true,
    "publicClient": false,
    "protocol": "openid-connect"
  }'
```

## Update Existing Client Secret

### Via Admin Console:
1. **Clients** → Select your client
2. **Credentials** tab
3. Click **Regenerate**
4. Copy new secret
5. Update your application.properties:
```properties
spring.security.oauth2.client.registration.keycloak.client-secret=NEW_SECRET_HERE
```

### Via REST API:
```bash
curl -X POST "http://localhost:8080/admin/realms/nngc-realm/clients/{client-id}/client-secret" \
  -H "Authorization: Bearer {admin_token}"
```

## Test New Credentials

```bash
# Test with curl
curl -X POST "http://localhost:8080/realms/nngc-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=YOUR_CLIENT_ID" \
  -d "client_secret=YOUR_NEW_SECRET" \
  -d "scope=openid profile"
```

## Security Best Practices

1. **Never commit secrets to git**
   - Use environment variables
   - Use .env files (gitignored)
   - Use secret management tools

2. **Rotate secrets regularly**
   - Set expiration policies
   - Monitor usage
   - Audit access logs

3. **Use different secrets per environment**
   - Dev: `dev-secret`
   - Staging: `staging-secret`
   - Production: Use vault/secret manager

## Environment Variables Setup

### Create .env file:
```bash
KEYCLOAK_CLIENT_ID=api-gateway
KEYCLOAK_CLIENT_SECRET=your-actual-secret
KEYCLOAK_REALM=nngc-realm
KEYCLOAK_URL=http://localhost:8080
```

### Update application.properties:
```properties
spring.security.oauth2.client.registration.keycloak.client-id=${KEYCLOAK_CLIENT_ID}
spring.security.oauth2.client.registration.keycloak.client-secret=${KEYCLOAK_CLIENT_SECRET}
```

## Troubleshooting

### Invalid client credentials
- Verify client exists in correct realm
- Check client authentication is enabled
- Ensure secret matches exactly (no spaces)
- Verify grant type is allowed

### Client not found
- Check realm name is correct
- Verify client_id spelling
- Ensure client is enabled

### Unauthorized grant type
- Enable "Service accounts roles" for client_credentials
- Enable "Direct access grants" for password grant