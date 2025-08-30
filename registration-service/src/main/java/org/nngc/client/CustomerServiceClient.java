package org.nngc.client;

import org.nngc.registration.ApiResponse;
import org.nngc.registration.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class CustomerServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceClient.class);
    
    private final WebClient.Builder webClientBuilder;
    private static final String FAILED="FAILED";

    public CustomerServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    private WebClient getCustomerServiceClient() {
        return webClientBuilder.baseUrl("http://customer-service").build();
    }
    
    
    public Mono<ApiResponse> registerCustomer(RegistrationRequest request) {
        logger.info("Registering customer via Customer Service: {}", request.getEmail());
        
        return getCustomerServiceClient()
                .post()
                .uri("/api/customers/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Customer registration completed successfully"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Failed to register customer with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("Failed to register customer: " + ex.getResponseBodyAsString())
                            .status(FAILED)
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Failed to register customer", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Customer service unavailable")
                            .status(FAILED)
                            .build());
                });
    }
    
    public Mono<ApiResponse> confirmEmailToken(String token) {
        logger.info("Confirming email token via Customer Service");
        
        return getCustomerServiceClient()
                .post()
                .uri("/api/customers/confirm-email?token={token}", token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Token confirmation completed"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Token confirmation failed with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("Invalid or expired token")
                            .status(FAILED)
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Token confirmation failed", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Customer service unavailable")
                            .status(FAILED)
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
                            .status(FAILED)
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Resend verification failed for: {}", email, ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Service unavailable")
                            .status(FAILED)
                            .build());
                });
    }
    
    public Mono<ApiResponse> getTokenStatus(String token) {
        logger.info("Checking token status via Customer Service");
        
        return getCustomerServiceClient()
                .get()
                .uri("/api/customers/token-status?token={token}", token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Token status check completed"))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Token status check failed with status: {}", ex.getStatusCode());
                    return Mono.just(ApiResponse.builder()
                            .message("invalid")
                            .status(FAILED)
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Token status check failed", ex);
                    return Mono.just(ApiResponse.builder()
                            .message("invalid")
                            .status(FAILED)
                            .build());
                });
    }
}