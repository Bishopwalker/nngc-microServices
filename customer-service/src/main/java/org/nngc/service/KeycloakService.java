package org.nngc.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.nngc.response.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.*;

@Service
public class KeycloakService {
    
    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);
    
    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String authServerUrl;
    
    @Value("${keycloak.realm:nngc-realm}")
    private String realm;
    
    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;
    
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    
    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;
    
    private Keycloak keycloakAdmin;
    
    private Keycloak getKeycloakAdmin() {
        if (keycloakAdmin == null) {
            keycloakAdmin = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm("master")
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId(adminClientId)
                    .username(adminUsername)
                    .password(adminPassword)
                    .build();
        }
        return keycloakAdmin;
    }
    
    public String createUser(RegistrationRequest request) {
        try {
            Keycloak keycloak = getKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            // Check if user already exists
            List<UserRepresentation> existingUsers = usersResource.search(request.getEmail());
            if (!existingUsers.isEmpty()) {
                logger.warn("User with email {} already exists in Keycloak", request.getEmail());
                return existingUsers.get(0).getId();
            }
            
            // Create new user representation
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(false); // Will be enabled after email verification
            user.setUsername(request.getEmail());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailVerified(false);
            
            // Set user attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("phone", Collections.singletonList(request.getPhone()));
            attributes.put("houseNumber", Collections.singletonList(request.getHouseNumber()));
            attributes.put("streetName", Collections.singletonList(request.getStreetName()));
            attributes.put("city", Collections.singletonList(request.getCity()));
            attributes.put("state", Collections.singletonList(request.getState()));
            attributes.put("zipCode", Collections.singletonList(request.getZipCode()));
            attributes.put("service", Collections.singletonList(request.getService()));
            user.setAttributes(attributes);
            
            // Create user
            Response response = usersResource.create(user);
            
            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                logger.info("Successfully created user in Keycloak with ID: {}", userId);
                
                // Set password
                CredentialRepresentation passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                passwordCred.setValue(request.getPassword());
                
                UserResource userResource = usersResource.get(userId);
                userResource.resetPassword(passwordCred);
                
                // Assign default role
                assignUserRole(userId, "user");
                
                response.close();
                return userId;
            } else {
                logger.error("Failed to create user in Keycloak. Status: {}", response.getStatus());
                response.close();
                throw new RuntimeException("Failed to create user in Keycloak");
            }
        } catch (Exception e) {
            logger.error("Error creating user in Keycloak: ", e);
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage());
        }
    }
    
    public void enableUser(String email) {
        try {
            Keycloak keycloak = getKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            List<UserRepresentation> users = usersResource.search(email);
            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                user.setEnabled(true);
                user.setEmailVerified(true);
                usersResource.get(user.getId()).update(user);
                logger.info("Enabled user in Keycloak: {}", email);
            } else {
                logger.warn("User not found in Keycloak: {}", email);
            }
        } catch (Exception e) {
            logger.error("Error enabling user in Keycloak: ", e);
            throw new RuntimeException("Failed to enable user in Keycloak: " + e.getMessage());
        }
    }
    
    public void assignUserRole(String userId, String roleName) {
        try {
            Keycloak keycloak = getKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            
            // Get realm role
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            
            // Assign role to user
            userResource.roles().realmLevel().add(Collections.singletonList(role));
            logger.info("Assigned role {} to user {}", roleName, userId);
        } catch (Exception e) {
            logger.error("Error assigning role to user: ", e);
        }
    }
    
    public void deleteUser(String email) {
        try {
            Keycloak keycloak = getKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            List<UserRepresentation> users = usersResource.search(email);
            if (!users.isEmpty()) {
                usersResource.delete(users.get(0).getId());
                logger.info("Deleted user from Keycloak: {}", email);
            }
        } catch (Exception e) {
            logger.error("Error deleting user from Keycloak: ", e);
            throw new RuntimeException("Failed to delete user from Keycloak: " + e.getMessage());
        }
    }
    
    public UserRepresentation getUserByEmail(String email) {
        try {
            Keycloak keycloak = getKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            List<UserRepresentation> users = usersResource.search(email);
            if (!users.isEmpty()) {
                return users.get(0);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting user from Keycloak: ", e);
            return null;
        }
    }
}