package org.nngc.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Token {
     @Id
    @SequenceGenerator(
            name = "token_id_seq",
            sequenceName = "token_id_seq",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_id_seq"
    )
    public long id;

    public String token;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    public TokenType tokenType = TokenType.EMAIL_VERIFICATION;

    public boolean revoked;

    public boolean expired;
    public LocalDateTime createdAt;
    public LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @Column(name = "customer_id")
    public Long customerId;
}