package nngc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiGatewaySecurityTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldAllowPublicRegistrationEndpoint() {
        webTestClient
            .post()
            .uri("/auth/nngc/registration")
            .exchange()
            .expectStatus().is5xxServerError(); // Service not running, but security allows it
    }

    @Test
    void shouldAllowPublicConfirmationEndpoint() {
        webTestClient
            .get()
            .uri("/auth/nngc/confirm?token=test")
            .exchange()
            .expectStatus().is5xxServerError(); // Service not running, but security allows it
    }

    @Test
    void shouldAllowPublicTokenStatusEndpoint() {
        webTestClient
            .get()
            .uri("/auth/nngc/token_status?token=test")
            .exchange()
            .expectStatus().is5xxServerError(); // Service not running, but security allows it
    }

    @Test
    void shouldAllowPublicHealthEndpoint() {
        webTestClient
            .get()
            .uri("/auth/nngc/health")
            .exchange()
            .expectStatus().is5xxServerError(); // Service not running, but security allows it
    }

    @Test
    void shouldRejectProtectedEndpointWithoutAuth() {
        webTestClient
            .get()
            .uri("/auth/nngc/resend-token/test@example.com")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void shouldRejectCustomerEndpointWithoutAuth() {
        webTestClient
            .get()
            .uri("/api/customers/1")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void shouldAllowProtectedEndpointWithValidJWT() {
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockJwt()
                .jwt(jwt -> jwt
                    .issuer("http://localhost:8080/realms/nngc-realm")
                    .subject("test-user")
                    .claim("scope", "openid profile")))
            .get()
            .uri("/auth/nngc/resend-token/test@example.com")
            .exchange()
            .expectStatus().is5xxServerError(); // Service not running, but security allows it
    }

    @Test
    void shouldAllowCustomerEndpointWithValidJWT() {
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockJwt()
                .authorities(Collections.singletonList(() -> "SCOPE_openid")))
            .get()
            .uri("/api/customers/1")
            .exchange()
            .expectStatus().is5xxServerError(); // Service not running, but security allows it
    }

    @Test
    void shouldHandleCORSPreflightRequest() {
        webTestClient
            .options()
            .uri("/auth/nngc/registration")
            .header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "Content-Type,Authorization")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("Access-Control-Allow-Origin")
            .expectHeader().exists("Access-Control-Allow-Methods");
    }

    @Test
    void shouldAllowActuatorEndpoints() {
        webTestClient
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }
}