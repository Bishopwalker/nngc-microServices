package org.nngc.client;

import org.nngc.dto.EmailRequest;
import org.nngc.dto.WelcomeEmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EmailServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceClient.class);
    
    private final WebClient webClient;

    public EmailServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public Mono<Void> sendRegistrationEmail(String email, String firstName, String confirmationLink) {
        EmailRequest request = new EmailRequest(email, firstName, confirmationLink);
        
        return webClient.post()
                .uri("http://email-service/email/send-registration")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.info("Registration email sent to: {}", email))
                .doOnError(error -> logger.error("Error sending registration email: ", error));
    }
    
    public Mono<Void> sendPasswordResetEmail(String email, String firstName, String resetLink) {
        EmailRequest request = new EmailRequest(email, firstName, resetLink);
        
        return webClient.post()
                .uri("http://email-service/email/send-password-reset")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.info("Password reset email sent to: {}", email))
                .doOnError(error -> logger.error("Error sending password reset email: ", error));
    }
    
    public Mono<Void> sendWelcomeEmail(String email, String firstName) {
        WelcomeEmailRequest request = new WelcomeEmailRequest(email, firstName);
        
        return webClient.post()
                .uri("http://email-service/email/send-welcome")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.info("Welcome email sent to: {}", email))
                .doOnError(error -> logger.error("Error sending welcome email: ", error));
    }
}