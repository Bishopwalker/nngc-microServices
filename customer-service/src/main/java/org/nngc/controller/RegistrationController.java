package org.nngc.controller;

import org.nngc.response.ApiResponse;
import org.nngc.response.RegistrationRequest;
import org.nngc.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/auth/customer")
@CrossOrigin(origins = "*")
public class RegistrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    
    @Autowired
    private RegistrationService registrationService;
    
    @Value("${application.frontend-url:http://localhost:5173}")
    private String frontendUrl;
    
    @Value("${application.production-frontend-url:https://northernneckgarbage.com}")
    private String prodFrontendUrl;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse>> register(@RequestBody RegistrationRequest request) {
        logger.info("Registration request received for email: {}", request.getEmail());
        
        return registrationService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> {
                    logger.error("Registration failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Registration failed: " + error.getMessage())
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @PostMapping("/resend-verification")
    public Mono<ResponseEntity<ApiResponse>> resendVerification(@RequestParam String email) {
        logger.info("Resend verification request for email: {}", email);
        
        return registrationService.resendVerificationEmail(email)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> {
                    logger.error("Resend verification failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Failed to resend verification: " + error.getMessage())
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @GetMapping("/confirm")
    public void confirmEmail(@RequestParam("token") String token, HttpServletResponse response) 
            throws IOException {
        logger.info("Email confirmation request received");
        
        registrationService.confirmEmail(token)
                .subscribe(
                    result -> {
                        try {
                            String redirectUrl = getRedirectUrl(result.getStatus());
                            response.sendRedirect(redirectUrl);
                        } catch (IOException e) {
                            logger.error("Error redirecting after email confirmation: ", e);
                        }
                    },
                    error -> {
                        try {
                            logger.error("Email confirmation failed: ", error);
                            String errorUrl = isProduction() ? 
                                prodFrontendUrl + "/email-verification-failed" :
                                frontendUrl + "/email-verification-failed";
                            response.sendRedirect(errorUrl);
                        } catch (IOException e) {
                            logger.error("Error redirecting after email confirmation error: ", e);
                        }
                    }
                );
    }
    
    @GetMapping("/token-status")
    public Mono<ResponseEntity<ApiResponse>> getTokenStatus(@RequestParam("token") String token) {
        logger.info("Token status check request received");
        
        return registrationService.confirmEmail(token)
                .map(response -> {
                    String status = switch (response.getStatus()) {
                        case "SUCCESS" -> "valid";
                        case "ALREADY_CONFIRMED" -> "already_confirmed";
                        case "EXPIRED" -> "expired";
                        default -> "invalid";
                    };
                    
                    ApiResponse statusResponse = ApiResponse.builder()
                            .message(status)
                            .status(response.getStatus())
                            .build();
                    
                    return ResponseEntity.ok(statusResponse);
                })
                .onErrorResume(error -> {
                    logger.error("Token status check failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("invalid")
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.ok(errorResponse));
                });
    }
    
    private String getRedirectUrl(String status) {
        String baseUrl = isProduction() ? prodFrontendUrl : frontendUrl;
        
        return switch (status) {
            case "SUCCESS" -> baseUrl + "/email-verification-success";
            case "ALREADY_CONFIRMED" -> baseUrl + "/email-already-confirmed";
            case "EXPIRED" -> baseUrl + "/email-verification-expired";
            default -> baseUrl + "/email-verification-failed";
        };
    }
    
    private boolean isProduction() {
        return "prod".equals(activeProfile) || "production".equals(activeProfile);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Registration Service is running");
    }
}