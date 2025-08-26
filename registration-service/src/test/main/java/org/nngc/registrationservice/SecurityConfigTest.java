package org.nngc.registrationservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegistrationService registrationService;

    @Test
    void shouldAllowAllEndpointsWithoutAuthentication() {
        // Mock service responses
        ApiResponse response = ApiResponse.builder()
            .message("Success")
            .status("SUCCESS")
            .build();

        when(registrationService.register(any(RegistrationRequest.class)))
            .thenReturn(Mono.just(response));
        when(registrationService.confirmEmail(anyString()))
            .thenReturn(Mono.just(response));
        when(registrationService.getTokenStatus(anyString()))
            .thenReturn(Mono.just(response));
        when(registrationService.resendVerificationEmail(anyString()))
            .thenReturn(Mono.just(response));

        RegistrationRequest request = new RegistrationRequest(
            "John", "Doe", "john@example.com", "password123",
            "555-0123", "123", "Main St", "City", "VA", "Weekly", "12345"
        );

        // All endpoints should be accessible without authentication
        webTestClient
            .post()
            .uri("/auth/nngc/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated();

        webTestClient
            .get()
            .uri("/auth/nngc/confirm?token=test-token")
            .exchange()
            .expectStatus().is3xxRedirection();

        webTestClient
            .get()
            .uri("/auth/nngc/token_status?token=test-token")
            .exchange()
            .expectStatus().isOk();

        webTestClient
            .get()
            .uri("/auth/nngc/health")
            .exchange()
            .expectStatus().isOk();

        webTestClient
            .get()
            .uri("/auth/nngc/resend-token/john@example.com")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void shouldAllowActuatorEndpointsWithoutAuthentication() {
        webTestClient
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void shouldDisableCSRF() {
        // CSRF should be disabled for stateless API
        RegistrationRequest request = new RegistrationRequest(
            "John", "Doe", "john@example.com", "password123",
            "555-0123", "123", "Main St", "City", "VA", "Weekly", "12345"
        );

        when(registrationService.register(any(RegistrationRequest.class)))
            .thenReturn(Mono.just(ApiResponse.builder()
                .message("Success")
                .status("SUCCESS")
                .build()));

        webTestClient
            .post()
            .uri("/auth/nngc/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated();
    }
}