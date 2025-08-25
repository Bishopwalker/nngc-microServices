package org.nngc.registrationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class CustomerServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceClient.class);
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private WebClient getWebClient() {
        return webClientBuilder.baseUrl("http://customer-service").build();
    }
    
    public Mono<ApiResponse> register(RegistrationRequest request) {
        logger.info("Delegating registration to customer service for: {}", request.getEmail());
        
        return getWebClient()
                .post()
                .uri("/auth/customer/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Registration successful for: {}", request.getEmail()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Registration failed with status: {} for: {}", ex.getStatusCode(), request.getEmail());
                    return Mono.just(ApiResponse.builder()
                            .message("Registration failed: " + ex.getResponseBodyAsString())
                            .status("FAILED")
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Registration failed for: {}", request.getEmail(), ex);
                    return Mono.just(ApiResponse.builder()
                            .message("Registration service unavailable")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ApiResponse> resendVerificationEmail(String email) {
        logger.info("Delegating resend verification to customer service for: {}", email);
        
        return getWebClient()
                .post()
                .uri("/auth/customer/resend-verification?email={email}", email)
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
                            .message("Registration service unavailable")
                            .status("FAILED")
                            .build());
                });
    }
    
    public Mono<ResponseEntity<Void>> confirmEmail(String token) {
        logger.info("Delegating email confirmation to customer service");
        
        return getWebClient()
                .get()
                .uri("/auth/customer/confirm?token={token}", token)
                .exchangeToMono(response -> {
                    HttpStatus status = (HttpStatus) response.statusCode();
                    if (status.is3xxRedirection()) {
                        String location = response.headers().header("Location").stream().findFirst().orElse("");
                        return Mono.just(ResponseEntity.status(status)
                                .header("Location", location)
                                .build());
                    } else {
                        return Mono.just(ResponseEntity.status(status).build());
                    }
                })
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Email confirmation processed"))
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Email confirmation failed", ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
    
    public Mono<ApiResponse> getTokenStatus(String token) {
        logger.info("Delegating token status check to customer service");
        
        return getWebClient()
                .get()
                .uri("/auth/customer/token-status?token={token}", token)
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