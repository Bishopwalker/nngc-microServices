package org.nngc.registrationservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    
    private final CustomerServiceClient customerServiceClient;
    
    @Value("${application.base-url:https://api.northernneckgarbage.com}")
    private String baseUrl;
    
    public Mono<ApiResponse> resendVerificationEmail(String email) {
        log.info("Processing resend verification request for: {}", email);
        
        // Call customer service to handle resending verification
        return customerServiceClient.resendVerificationEmail(email)
                .doOnSuccess(response -> log.info("Resend verification completed for: {}", email))
                .onErrorResume(error -> {
                    log.error("Failed to resend verification for: {}", email, error);
                    return Mono.just(ApiResponse.builder()
                            .message("Failed to resend verification: " + error.getMessage())
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> register(RegistrationRequest request) {
        log.info("Processing registration for: {}", request.getEmail());
        
        // Validate request
        if (!isValidEmail(request.getEmail())) {
            return Mono.error(new IllegalArgumentException("Invalid email format"));
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            return Mono.error(new IllegalArgumentException("Password must be at least 8 characters long"));
        }
        
        // Create customer via Customer Service
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("firstName", request.getFirstName());
        customerData.put("lastName", request.getLastName());
        customerData.put("email", request.getEmail().toLowerCase());
        customerData.put("password", request.getPassword());
        customerData.put("phone", request.getPhone());
        customerData.put("houseNumber", request.getHouseNumber());
        customerData.put("streetName", request.getStreetName());
        customerData.put("city", request.getCity());
        customerData.put("state", request.getState());
        customerData.put("zipCode", request.getZipCode());
        customerData.put("service", request.getService());
        customerData.put("enabled", false);
        
        return customerServiceClient.createCustomer(customerData)
                .flatMap(customerResponse -> {
                    if ("SUCCESS".equals(customerResponse.getStatus())) {
                        // Generate verification token via Token Service
                        return customerServiceClient.generateVerificationToken(customerResponse.getCustomerDTO())
                                .flatMap(tokenResponse -> {
                                    String token = tokenResponse.getToken() != null && !tokenResponse.getToken().isEmpty() 
                                        ? tokenResponse.getToken().get(0) : null;
                                    
                                    if (token == null) {
                                        return Mono.error(new RuntimeException("Failed to generate verification token"));
                                    }
                                    
                                    // Send verification email via Email Service
                                    String confirmationLink = baseUrl + "/auth/nngc/confirm?token=" + token;
                                    return customerServiceClient.sendVerificationEmail(
                                            request.getEmail(),
                                            request.getFirstName(),
                                            confirmationLink
                                    ).then(Mono.just(ApiResponse.builder()
                                            .message("Registration successful. Please check your email for verification.")
                                            .token(Collections.singletonList(token))
                                            .customerDTO(customerResponse.getCustomerDTO())
                                            .status("SUCCESS")
                                            .build()));
                                });
                    } else {
                        return Mono.error(new RuntimeException("Failed to create customer: " + customerResponse.getMessage()));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Registration failed for: {}", request.getEmail(), error);
                    return Mono.just(ApiResponse.builder()
                            .message("Registration failed: " + error.getMessage())
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> confirmEmail(String token) {
        log.info("Processing email confirmation with token");
        
        return customerServiceClient.confirmEmailToken(token)
                .doOnSuccess(response -> log.info("Email confirmation completed"))
                .onErrorResume(error -> {
                    log.error("Email confirmation failed", error);
                    return Mono.just(ApiResponse.builder()
                            .message("Invalid or expired token")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> getTokenStatus(String token) {
        log.info("Processing token status check");
        
        return customerServiceClient.getTokenStatus(token)
                .doOnSuccess(response -> log.info("Token status check completed"))
                .onErrorResume(error -> {
                    log.error("Token status check failed", error);
                    return Mono.just(ApiResponse.builder()
                            .message("invalid")
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