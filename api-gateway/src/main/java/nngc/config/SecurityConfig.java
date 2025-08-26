package nngc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                        .jwkSetUri("http://localhost:8080/realms/nngc-realm/protocol/openid-connect/certs")
                    )
                )
                .authorizeExchange(exchanges -> exchanges
                    // Public endpoints - no authentication required
                    .pathMatchers(
                        "/auth/nngc/registration",
                        "/auth/nngc/confirm",
                        "/auth/nngc/token_status",
                        "/auth/nngc/health",
                        "/actuator/**",
                        "/api/auth/login",
                        "/api/auth/register"
                    ).permitAll()
                    
                    // Protected endpoints - require authentication
                    .pathMatchers(
                        "/auth/nngc/resend-token/**",
                        "/api/customers/**",
                        "/api/email/**",
                        "/api/payments/**",
                        "/api/google/**"
                    ).authenticated()
                    
                    // All other requests require authentication
                    .anyExchange().authenticated()
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}