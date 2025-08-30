package org.nngc.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nngc.client.CustomerServiceClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    
    private final CustomerServiceClient customerServiceClient;
    
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
        
        // Basic validation
        if (!isValidEmail(request.getEmail())) {
            return Mono.error(new IllegalArgumentException("Invalid email format"));
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            return Mono.error(new IllegalArgumentException("Password must be at least 8 characters long"));
        }
        
        // Delegate entire registration process to Customer Service
        return customerServiceClient.registerCustomer(request)
                .doOnSuccess(response -> log.info("Registration completed for: {}", request.getEmail()))
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