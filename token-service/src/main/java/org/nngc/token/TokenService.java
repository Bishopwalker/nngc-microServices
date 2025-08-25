package org.nngc.token;

import org.nngc.dto.CustomerDTO;
import org.nngc.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final CustomerServiceClient customerServiceClient;



    public String generateEmailVerificationToken(Long customerId) {
        var verificationToken = java.util.UUID.randomUUID().toString();
        var token = Token.builder()
                .customerId(customerId)
                .token(verificationToken)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expired(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(45))
                .revoked(false)
                .build();
        tokenRepository.save(token);
        return verificationToken;
    }







    public CustomerDTO confirmEmailVerificationToken(String token) {
        var userToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        if (userToken.getConfirmedAt() != null)
            throw new RuntimeException("Token already confirmed");
        if(LocalDateTime.now().isAfter(userToken.getExpiresAt()))
            throw new RuntimeException("Token expired");

        // Call customer service to enable the user
        var customer = customerServiceClient.enableCustomer(userToken.getCustomerId());
        
        userToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(userToken);
        
        log.info("Email verification completed for customer: {}", userToken.getCustomerId());
        
        return customer;
    }
}
