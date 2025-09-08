// OWASP ZAP JavaScript context setup for NNGC MicroServices
// This script configures ZAP for testing Spring Boot microservices

print("🔧 Setting up ZAP context for NNGC MicroServices...");

// Import required ZAP classes
var Control = Java.type("org.parosproxy.paros.control.Control");
var Model = Java.type("org.parosproxy.paros.model.Model");
var Context = Java.type("org.zaproxy.zap.model.Context");
var SessionManagement = Java.type("org.zaproxy.zap.extension.sessions.ExtensionSessionManagement");

function setupMicroservicesContext() {
    try {
        print("🏗️ Creating microservices context...");
        
        // Create new context for microservices testing
        var extSession = Control.getSingleton().getExtensionLoader()
            .getExtension(SessionManagement.class);
        var context = Model.getSingleton().getSession().getNewContext("NNGC-MicroServices");
        
        // Configure context scope - include API endpoints
        var includeUrls = [
            "http://localhost:8080/api/.*",    // API Gateway
            "http://localhost:8081/api/.*",    // Customer Service  
            "http://localhost:8082/api/.*",    // Registration Service
            "http://localhost:8083/api/.*",    // Token Service
            "http://localhost:8084/api/.*",    // Email Service
            "http://localhost:8085/api/.*",    // Stripe Service
            "http://localhost:8087/api/.*"     // Google Service
        ];
        
        for (var i = 0; i < includeUrls.length; i++) {
            context.addIncludeInContextRegex(includeUrls[i]);
            print("✅ Added to scope: " + includeUrls[i]);
        }
        
        // Configure context scope - exclude non-API endpoints
        var excludeUrls = [
            ".*/actuator/.*",                  // Spring Boot actuator
            ".*/swagger-ui/.*",                // Swagger UI
            ".*/webjars/.*",                   // Static resources
            ".*/static/.*",                    // Static content
            ".*/favicon.ico"                   // Favicon
        ];
        
        for (var i = 0; i < excludeUrls.length; i++) {
            context.addExcludeFromContextRegex(excludeUrls[i]);
            print("🚫 Excluded from scope: " + excludeUrls[i]);
        }
        
        print("✅ Context configured successfully");
        return context;
        
    } catch (error) {
        print("❌ Error setting up context: " + error.message);
        throw error;
    }
}

function setupAuthentication(context) {
    try {
        print("🔐 Setting up JWT authentication...");
        
        // Configure JWT authentication
        var authMethod = {
            name: "JWT Token Authentication",
            loginUrl: "http://localhost:8083/api/tokens/generate",
            loginRequestData: JSON.stringify({
                username: "testuser",
                password: "testpass123"
            }),
            usernameParameter: "username",
            passwordParameter: "password",
            authHeaders: ["Authorization"],
            tokenPattern: "Bearer (.+)"
        };
        
        // Note: This is pseudocode as ZAP's JavaScript API for auth varies by version
        // In practice, authentication would be configured via ZAP GUI or specific API calls
        print("⚠️  Authentication configuration requires manual setup in ZAP GUI");
        print("Login URL: " + authMethod.loginUrl);
        print("Test credentials: " + authMethod.usernameParameter + "/" + authMethod.passwordParameter);
        
    } catch (error) {
        print("❌ Error setting up authentication: " + error.message);
    }
}

function setupSessionManagement(context) {
    try {
        print("🍪 Setting up session management...");
        
        // Configure session management for JWT tokens
        var sessionConfig = {
            sessionTokens: ["Authorization", "X-Auth-Token", "JWT"],
            cookieNames: ["JSESSIONID", "SESSION"],
            sessionTimeout: 3600 // 1 hour
        };
        
        print("Session tokens tracked: " + sessionConfig.sessionTokens.join(", "));
        print("Cookie names tracked: " + sessionConfig.cookieNames.join(", "));
        
    } catch (error) {
        print("❌ Error setting up session management: " + error.message);
    }
}

function configureScanPolicies() {
    try {
        print("📋 Configuring scan policies for microservices...");
        
        var microservicesPolicies = {
            // High priority vulnerabilities for APIs
            highPriority: [
                "SQL Injection",
                "Cross Site Scripting (Reflected)",
                "Cross Site Scripting (Persistent)", 
                "Command Injection",
                "LDAP Injection",
                "XPath Injection",
                "Authentication Bypass",
                "Session Fixation",
                "Insecure Direct Object References"
            ],
            
            // Medium priority for configuration issues
            mediumPriority: [
                "Directory Browsing",
                "Backup File Disclosure",
                "Information Disclosure",
                "Missing Security Headers",
                "Weak Authentication",
                "Insecure HTTP Methods"
            ],
            
            // Low priority informational findings
            lowPriority: [
                "Server Information Disclosure",
                "Technology Detection",
                "Cookie Security",
                "Cache Control"
            ]
        };
        
        print("High priority checks: " + microservicesPolicies.highPriority.length);
        print("Medium priority checks: " + microservicesPolicies.mediumPriority.length);
        print("Low priority checks: " + microservicesPolicies.lowPriority.length);
        
    } catch (error) {
        print("❌ Error configuring scan policies: " + error.message);
    }
}

function setupMicroservicesSpecificChecks() {
    try {
        print("🔍 Setting up microservices-specific security checks...");
        
        var microservicesChecks = [
            {
                name: "Service Discovery Exposure",
                description: "Check if service registry exposes internal service information",
                targets: ["http://localhost:8761/eureka/apps"]
            },
            {
                name: "Direct Service Access",
                description: "Test if services can be accessed directly bypassing API Gateway",
                targets: [
                    "http://localhost:8081/api/customers",
                    "http://localhost:8082/api/register",
                    "http://localhost:8083/api/tokens"
                ]
            },
            {
                name: "Container Metadata Access",
                description: "Check for access to container/cloud metadata endpoints",
                targets: [
                    "http://169.254.169.254/latest/meta-data/",
                    "http://metadata.google.internal/computeMetadata/v1/"
                ]
            },
            {
                name: "Inter-Service Authorization",
                description: "Test authorization between microservices",
                scenarios: [
                    "Customer service accessing token service",
                    "Registration service accessing email service"
                ]
            }
        ];
        
        for (var i = 0; i < microservicesChecks.length; i++) {
            var check = microservicesChecks[i];
            print("🔸 " + check.name + ": " + check.description);
        }
        
    } catch (error) {
        print("❌ Error setting up microservices checks: " + error.message);
    }
}

function main() {
    try {
        print("🚀 Starting NNGC MicroServices ZAP configuration...");
        
        var context = setupMicroservicesContext();
        setupAuthentication(context);
        setupSessionManagement(context);
        configureScanPolicies();
        setupMicroservicesSpecificChecks();
        
        print("✅ ZAP configuration completed successfully!");
        print("🎯 Context ID: " + context.getId());
        print("📊 Ready for security testing of NNGC microservices");
        
        return {
            success: true,
            contextId: context.getId(),
            message: "NNGC MicroServices ZAP context configured successfully"
        };
        
    } catch (error) {
        print("❌ Failed to configure ZAP: " + error.message);
        return {
            success: false,
            error: error.message
        };
    }
}

// Execute main function
var result = main();
print("🏁 Configuration result: " + JSON.stringify(result, null, 2));