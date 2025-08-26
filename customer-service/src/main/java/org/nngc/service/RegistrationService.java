package org.nngc.service;

import org.nngc.client.EmailServiceClient;
import org.nngc.client.TokenServiceClient;
import org.nngc.dto.CustomerDTO;
import org.nngc.entity.Customer;
import org.nngc.exception.RegistrationException;
import org.nngc.repository.CustomerRepository;
import org.nngc.response.ApiResponse;
import org.nngc.response.RegistrationRequest;
import org.nngc.roles.AppUserRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class RegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;
    private final TokenServiceClient tokenServiceClient;
    private final EmailServiceClient emailServiceClient;
    private final String baseUrl;

    public RegistrationService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            KeycloakService keycloakService,
            TokenServiceClient tokenServiceClient,
            EmailServiceClient emailServiceClient,
            @Value("${application.base-url:https://api.northernneckgarbage.com}") String baseUrl) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.keycloakService = keycloakService;
        this.tokenServiceClient = tokenServiceClient;
        this.emailServiceClient = emailServiceClient;
        this.baseUrl = baseUrl;
    }
    
    public Mono<ApiResponse> register(RegistrationRequest request) {
        logger.info("Processing registration for: {}", request.getEmail());
        
        // Validate email
        if (!isValidEmail(request.getEmail())) {
            return Mono.error(new IllegalArgumentException("Invalid email format"));
        }
        
        // Check if user already exists
        Optional<Customer> existingCustomer = customerRepository.findByEmail(request.getEmail().toLowerCase());
        if (existingCustomer.isPresent()) {
            return Mono.error(new IllegalStateException("User with this email already exists"));
        }
        
        // Validate password
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            return Mono.error(new IllegalArgumentException("Password must be at least 8 characters long"));
        }
        
        try {
            // Create user in Keycloak first
            String keycloakUserId = keycloakService.createUser(request);
            
            // Create customer in database
            Customer customer = Customer.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail().toLowerCase())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phone(request.getPhone())
                    .houseNumber(request.getHouseNumber())
                    .streetName(request.getStreetName())
                    .city(request.getCity())
                    .state(request.getState())
                    .zipCode(request.getZipCode())
                    .service(request.getService())
                    .appUserRoles(AppUserRoles.USER)
                    .enabled(false) // Will be enabled after email verification
                    .keycloakUserId(keycloakUserId)
                    .build();
            
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Customer saved with ID: {}", savedCustomer.getId());
            
            // Generate JWT token for email verification
            CustomerDTO customerDTO = savedCustomer.toCustomerDTO();
            
            return tokenServiceClient.generateToken(customerDTO)
                    .flatMap(token -> {
                        // Save token
                        return tokenServiceClient.saveUserToken(customerDTO, token)
                                .then(Mono.just(token));
                    })
                    .flatMap(token -> {
                        // Send verification email
                        String confirmationLink = baseUrl + "/auth/nngc/confirm?token=" + token;
                        return emailServiceClient.sendRegistrationEmail(
                                request.getEmail(),
                                request.getFirstName(),
                                confirmationLink
                        ).then(Mono.just(token));
                    })
                    .map(token -> ApiResponse.builder()
                            .message("Registration successful. Please check your email for verification.")
                            .token(Collections.singletonList(token))
                            .customerDTO(customerDTO)
                            .status("SUCCESS")
                            .build())
                    .onErrorResume(error -> {
                        logger.error("Error during registration: ", error);
                        // Rollback Keycloak user if database save failed
                        if (keycloakUserId != null) {
                            try {
                                keycloakService.deleteUser(request.getEmail());
                            } catch (Exception e) {
                                logger.error("Failed to rollback Keycloak user: ", e);
                            }
                        }
                        return Mono.error(error);
                    });
            
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            return Mono.error(new RegistrationException("Registration failed: " + e.getMessage(), e));
        }
    }
    
    public Mono<ApiResponse> resendVerificationEmail(String email) {
        logger.info("Resending verification email for: {}", email);
        
        Optional<Customer> customerOpt = customerRepository.findByEmail(email.toLowerCase());
        if (customerOpt.isEmpty()) {
            return Mono.error(new IllegalArgumentException("User not found"));
        }
        
        Customer customer = customerOpt.get();
        if (customer.isEnabled()) {
            return Mono.just(ApiResponse.builder()
                    .message("Account is already verified")
                    .status("ALREADY_VERIFIED")
                    .build());
        }
        
        CustomerDTO customerDTO = customer.toCustomerDTO();
        
        // Revoke old tokens and generate new one
        return tokenServiceClient.revokeAllUserTokens(customer.getId())
                .then(tokenServiceClient.generateToken(customerDTO))
                .flatMap(token -> {
                    // Save new token
                    return tokenServiceClient.saveUserToken(customerDTO, token)
                            .then(Mono.just(token));
                })
                .flatMap(token -> {
                    // Send verification email
                    String confirmationLink = baseUrl + "/auth/nngc/confirm?token=" + token;
                    return emailServiceClient.sendRegistrationEmail(
                            email,
                            customer.getFirstName(),
                            confirmationLink
                    ).then(Mono.just(token));
                })
                .map(token -> ApiResponse.builder()
                        .message("Verification email sent successfully")
                        .token(Collections.singletonList(token))
                        .customerDTO(customerDTO)
                        .status("SUCCESS")
                        .build())
                .onErrorResume(error -> {
                    logger.error("Error resending verification email: ", error);
                    return Mono.error(new RegistrationException("Failed to resend verification email"));
                });
    }
    
    public Mono<ApiResponse> confirmEmail(String token) {
        logger.info("Confirming email with token");
        
        return tokenServiceClient.confirmToken(token)
                .map(response -> {
                    if ("SUCCESS".equals(response.getStatus())) {
                        // Get customer from token response
                        CustomerDTO customerDTO = response.getCustomerDTO();
                        if (customerDTO != null) {
                            // Enable user in database
                            Optional<Customer> customerOpt = customerRepository.findById(customerDTO.getId());
                            if (customerOpt.isPresent()) {
                                Customer customer = customerOpt.get();
                                customer.setEnabled(true);
                                customerRepository.save(customer);
                                
                                // Enable user in Keycloak
                                keycloakService.enableUser(customer.getEmail());
                                
                                // Send welcome email
                                emailServiceClient.sendWelcomeEmail(
                                        customer.getEmail(),
                                        customer.getFirstName()
                                ).subscribe();
                                
                                logger.info("Email confirmed for customer: {}", customer.getEmail());
                            }
                        }
                    }
                    return response;
                })
                .onErrorResume(error -> {
                    logger.error("Error confirming email: ", error);
                    return Mono.just(ApiResponse.builder()
                            .message("Invalid or expired token")
                            .status("FAILED")
                            .build());
                });
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}