package org.nngc.registrationservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegistrationService registrationService;

    private RegistrationRequest validRequest;
    private ApiResponse successResponse;

    @BeforeEach
    void setUp() {
        validRequest = new RegistrationRequest(
            "John", 
            "Doe", 
            "john.doe@example.com", 
            "password123", 
            "555-0123", 
            "123", 
            "Main St", 
            "Anytown", 
            "VA", 
            "Weekly", 
            "12345"
        );

        successResponse = ApiResponse.builder()
            .message("Registration successful. Please check your email for verification.")
            .status("SUCCESS")
            .token(Collections.singletonList("mock-token-123"))
            .build();
    }

    @Test
    void shouldAllowRegistrationWithoutAuthentication() {
        when(registrationService.register(any(RegistrationRequest.class)))
            .thenReturn(Mono.just(successResponse));

        webTestClient
            .post()
            .uri("/auth/nngc/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(validRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(ApiResponse.class)
            .value(response -> {
                assert response.getStatus().equals("SUCCESS");
                assert response.getMessage().contains("Registration successful");
                assert response.getToken() != null;
            });
    }

    @Test
    void shouldAllowEmailConfirmationWithoutAuthentication() {
        ApiResponse confirmResponse = ApiResponse.builder()
            .message("Email confirmed successfully")
            .status("SUCCESS")
            .build();

        when(registrationService.confirmEmail(anyString()))
            .thenReturn(Mono.just(confirmResponse));

        webTestClient
            .get()
            .uri("/auth/nngc/confirm?token=valid-token-123")
            .exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    void shouldAllowTokenStatusCheckWithoutAuthentication() {
        ApiResponse tokenResponse = ApiResponse.builder()
            .message("Token is valid")
            .status("SUCCESS")
            .build();

        when(registrationService.getTokenStatus(anyString()))
            .thenReturn(Mono.just(tokenResponse));

        webTestClient
            .get()
            .uri("/auth/nngc/token_status?token=valid-token-123")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(status -> assert status.equals("good"));
    }

    @Test
    void shouldAllowHealthCheckWithoutAuthentication() {
        webTestClient
            .get()
            .uri("/auth/nngc/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(response -> assert response.contains("Registration Service is running"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_openid")
    void shouldAllowResendTokenWithAuthentication() {
        ApiResponse resendResponse = ApiResponse.builder()
            .message("Verification email resent successfully")
            .status("SUCCESS")
            .build();

        when(registrationService.resendVerificationEmail(anyString()))
            .thenReturn(Mono.just(resendResponse));

        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockJwt()
                .authorities(Collections.singletonList(() -> "SCOPE_openid")))
            .get()
            .uri("/auth/nngc/resend-token/john.doe@example.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody(ApiResponse.class)
            .value(response -> {
                assert response.getStatus().equals("SUCCESS");
                assert response.getMessage().contains("resent successfully");
            });
    }

    @Test
    void shouldRejectResendTokenWithoutAuthentication() {
        webTestClient
            .get()
            .uri("/auth/nngc/resend-token/john.doe@example.com")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void shouldHandleRegistrationServiceErrors() {
        when(registrationService.register(any(RegistrationRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
            .post()
            .uri("/auth/nngc/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(validRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ApiResponse.class)
            .value(response -> {
                assert response.getStatus().equals("FAILED");
                assert response.getMessage().contains("Registration failed");
            });
    }

    @Test
    void shouldValidateRegistrationRequestFields() {
        // Test with invalid email
        RegistrationRequest invalidRequest = new RegistrationRequest(
            "John", "Doe", "invalid-email", "password123", "555-0123",
            "123", "Main St", "Anytown", "VA", "Weekly", "12345"
        );

        when(registrationService.register(any(RegistrationRequest.class)))
            .thenReturn(Mono.error(new IllegalArgumentException("Invalid email format")));

        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
            .post()
            .uri("/auth/nngc/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ApiResponse.class)
            .value(response -> {
                assert response.getStatus().equals("FAILED");
                assert response.getMessage().contains("Invalid email format");
            });
    }

    @Test
    void shouldHandleExpiredTokenStatus() {
        ApiResponse expiredResponse = ApiResponse.builder()
            .message("Token expired")
            .status("EXPIRED")
            .build();

        when(registrationService.getTokenStatus(anyString()))
            .thenReturn(Mono.just(expiredResponse));

        webTestClient
            .get()
            .uri("/auth/nngc/token_status?token=expired-token")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(status -> assert status.equals("expired"));
    }

    @Test
    void shouldHandleAlreadyConfirmedStatus() {
        ApiResponse alreadyConfirmedResponse = ApiResponse.builder()
            .message("Email already confirmed")
            .status("ALREADY_CONFIRMED")
            .build();

        when(registrationService.getTokenStatus(anyString()))
            .thenReturn(Mono.just(alreadyConfirmedResponse));

        webTestClient
            .get()
            .uri("/auth/nngc/token_status?token=confirmed-token")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(status -> assert status.equals("good"));
    }
}