package org.nngc.registrationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceClient.class);
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private WebClient getCustomerServiceClient() {
        return webClientBuilder.baseUrl("http://customer-service").build();
    }
    
    private WebClient getTokenServiceClient() {
        return webClientBuilder.baseUrl("http://token-service").build();
    }
    
    private WebClient getEmailServiceClient() {
        return webClientBuilder.baseUrl("http://email-service").build();
    }
    
    public Mono<ApiResponse> createCustomer(Map<String, Object> customerData) {
        logger.info("Creating customer in Customer Service");
        
        return getCustomerServiceClient()
                .post()
                .uri("/api/customers/create")
                .bodyValue(customerData)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Customer created successfully"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Failed to create customer with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("Failed to create customer: " + ex.getResponseBodyAsString())
                            .status("FAILED")
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Failed to create customer", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Customer service unavailable")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> generateVerificationToken(Object customerDTO) {
        logger.info("Generating verification token via Token Service");
        
        return getTokenServiceClient()
                .post()
                .uri("/api/tokens/generate")
                .bodyValue(customerDTO)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Token generated successfully"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Failed to generate token with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("Failed to generate token: " + ex.getResponseBodyAsString())
                            .status("FAILED")
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Failed to generate token", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Token service unavailable")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<Void> sendVerificationEmail(String email, String firstName, String confirmationLink) {
        logger.info("Sending verification email to: {}", email);
        
        Map<String, String> emailRequest = new HashMap<>();
        emailRequest.put("email", email);
        emailRequest.put("firstName", firstName);
        emailRequest.put("confirmationLink", confirmationLink);
        
        return getEmailServiceClient()
                .post()
                .uri("/api/email/send-verification")
                .bodyValue(emailRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Verification email sent successfully to: {}", email))
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Failed to send verification email to: {}", email, ex);
                    return Mono.empty();
                });
    }
    
    public Mono<ApiResponse> confirmEmailToken(String token) {
        logger.info("Confirming email token via Token Service");
        
        return getTokenServiceClient()
                .post()
                .uri("/api/tokens/confirm?token={token}", token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Token confirmation completed"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Token confirmation failed with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("Invalid or expired token")
                            .status("FAILED")
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Token confirmation failed", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Token service unavailable")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> resendVerificationEmail(String email) {
        logger.info("Resending verification email for: {}", email);
        
        return getCustomerServiceClient()
                .post()
                .uri("/api/customers/resend-verification?email={email}", email)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Resend verification successful for: {}", email))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Resend verification failed with status: {} for: {}", ex.getStatusCode(), email);
                    return Mono.just(ApiResponse.builder()
                            .message("Resend verification failed: " + ex.getResponseBodyAsString())
                            .status("FAILED")
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Resend verification failed for: {}", email, ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Service unavailable")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> getTokenStatus(String token) {
        logger.info("Checking token status via Token Service");
        
        return getTokenServiceClient()
                .get()
                .uri("/api/tokens/status?token={token}", token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Token status check completed"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Token status check failed with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("invalid")
                            .status("FAILED")
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Token status check failed", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("invalid")
                            .status("FAILED")
                            .build());
                });
    }
}