package org.nngc.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Query(value = """
            select t from Token t where t.customerId = :customerId 
            and t.expired = false and t.revoked = false
            """)
    Optional<Token> findValidTokenByCustomerId(Long customerId);
}