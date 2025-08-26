package org.nngc.client;

import org.nngc.dto.CustomerDTO;
import org.nngc.dto.TokenRequest;
import org.nngc.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TokenServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceClient.class);
    
    private final WebClient webClient;

    public TokenServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public Mono<ApiResponse> saveUserToken(CustomerDTO customer, String token) {
        return webClient.post()
                .uri("http://token-service/token/save")
                .bodyValue(new TokenRequest(customer.getId(), token))
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .doOnSuccess(response -> logger.info("Token saved successfully for customer: {}", customer.getEmail()))
                .doOnError(error -> logger.error("Error saving token: ", error));
    }
    
    public Mono<ApiResponse> revokeAllUserTokens(Long customerId) {
        return webClient.post()
                .uri("http://token-service/token/revoke-all/{customerId}", customerId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .doOnSuccess(response -> logger.info("Tokens revoked for customer ID: {}", customerId))
                .doOnError(error -> logger.error("Error revoking tokens: ", error));
    }
    
    public Mono<String> generateToken(CustomerDTO customer) {
        return webClient.post()
                .uri("http://token-service/token/generate")
                .bodyValue(customer)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(token -> logger.info("Token generated for customer: {}", customer.getEmail()))
                .doOnError(error -> logger.error("Error generating token: ", error));
    }
    
    public Mono<ApiResponse> confirmToken(String token) {
        return webClient.get()
                .uri("http://token-service/token/confirm?token={token}", token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .doOnSuccess(response -> logger.info("Token confirmed successfully"))
                .doOnError(error -> logger.error("Error confirming token: ", error));
    }
}