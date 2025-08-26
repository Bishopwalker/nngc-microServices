package org.nngc.registrationservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nngc.registrationservice.registration.ApiResponse;
import org.nngc.registrationservice.registration.RegistrationRequest;
import org.nngc.registrationservice.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth/nngc")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://northernneckgarbage.com", "https://www.northernneckgarbage.com", "http://localhost:3000"})
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    @Value("${application.frontend-url:http://localhost:5173}")
    private String frontendUrl;
    
    @Value("${application.production-frontend-url:https://northernneckgarbage.com}")
    private String prodFrontendUrl;
    
    @PostMapping("/registration")
    public Mono<ResponseEntity<ApiResponse>> register(@RequestBody RegistrationRequest request) {
        log.info("Registration request received for: {}", request.getEmail());
        
        return registrationService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> {
                    log.error("Registration failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Registration failed: " + error.getMessage())
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @GetMapping("/resend-token/{email}")
    public Mono<ResponseEntity<ApiResponse>> resendToken(@PathVariable String email) {
        log.info("Resend token request for: {}", email);
        
        return registrationService.resendVerificationEmail(email)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> {
                    log.error("Resend token failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Failed to resend token: " + error.getMessage())
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @GetMapping("/confirm")
    public void confirmMail(@RequestParam("token") String token, HttpServletResponse response) 
            throws IOException {
        log.info("Email confirmation request received");
        
        registrationService.confirmEmail(token)
                .subscribe(
                    result -> {
                        try {
                            String redirectUrl = getRedirectUrl(result.getStatus());
                            response.sendRedirect(redirectUrl);
                        } catch (IOException e) {
                            log.error("Error redirecting after email confirmation: ", e);
                        }
                    },
                    error -> {
                        try {
                            log.error("Email confirmation failed: ", error);
                            String errorUrl = isProduction() ? 
                                prodFrontendUrl + "/email-verification-failed" :
                                frontendUrl + "/email-verification-failed";
                            response.sendRedirect(errorUrl);
                        } catch (IOException e) {
                            log.error("Error redirecting after email confirmation error: ", e);
                        }
                    }
                );
    }
    
    @GetMapping("/token_status")
    public Mono<ResponseEntity<String>> tokenStatus(@RequestParam("token") String token) {
        log.info("Token status check request received");
        
        return registrationService.getTokenStatus(token)
                .map(response -> {
                    String status = "invalid";
                    if ("SUCCESS".equals(response.getStatus()) || "ALREADY_CONFIRMED".equals(response.getStatus())) {
                        status = "good";
                    } else if ("EXPIRED".equals(response.getStatus())) {
                        status = "expired";
                    }
                    
                    log.info("Token status response: {}", status);
                    return ResponseEntity.ok(status);
                })
                .onErrorResume(error -> {
                    log.error("Token status check failed: ", error);
                    return Mono.just(ResponseEntity.ok("invalid"));
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
        return ResponseEntity.ok("Registration Service is running");
    }
}