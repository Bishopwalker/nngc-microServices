package nngc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
    "spring.cloud.gateway.discovery.locator.enabled=false",
    "eureka.client.enabled=false",
    "management.health.circuitbreakers.enabled=false",
    "management.health.gateway.enabled=false"
})
class ApiGatewaySecurityTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldAllowPublicRegistrationEndpoint() {
        webTestClient
            .post()
            .uri("/auth/nngc/registration")
            .exchange()
            .expectStatus().isNotFound(); // Route not found when services are down
    }

    @Test
    void shouldAllowPublicConfirmationEndpoint() {
        webTestClient
            .get()
            .uri("/auth/nngc/confirm?token=test")
            .exchange()
            .expectStatus().isNotFound(); // Route not found when services are down
    }

    @Test
    void shouldAllowPublicTokenStatusEndpoint() {
        webTestClient
            .get()
            .uri("/auth/nngc/token_status?token=test")
            .exchange()
            .expectStatus().isNotFound(); // Route not found when services are down
    }

    @Test
    void shouldAllowPublicHealthEndpoint() {
        webTestClient
            .get()
            .uri("/auth/nngc/health")
            .exchange()
            .expectStatus().isNotFound(); // Route not found when services are down
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
            .expectStatus().isNotFound(); // Route not found when services are down
    }

    @Test
    void shouldAllowCustomerEndpointWithValidJWT() {
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockJwt()
                .authorities(Collections.singletonList(() -> "SCOPE_openid")))
            .get()
            .uri("/api/customers/1")
            .exchange()
            .expectStatus().isNotFound(); // Route not found when services are down
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