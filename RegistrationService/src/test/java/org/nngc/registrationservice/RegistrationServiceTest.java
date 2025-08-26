package org.nngc.registrationservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationRequest validRequest;
    private ApiResponse customerResponse;
    private ApiResponse tokenResponse;

    @BeforeEach
    void setUp() {
        validRequest = new RegistrationRequest(
            "John", "Doe", "john.doe@example.com", "password123",
            "555-0123", "123", "Main St", "Anytown", "VA", "Weekly", "12345"
        );

        // Mock customer DTO
        Map<String, Object> customerDTO = new HashMap<>();
        customerDTO.put("id", 1L);
        customerDTO.put("email", "john.doe@example.com");
        customerDTO.put("firstName", "John");
        customerDTO.put("lastName", "Doe");

        customerResponse = ApiResponse.builder()
            .message("Customer created successfully")
            .status("SUCCESS")
            .customerDTO(customerDTO)
            .build();

        tokenResponse = ApiResponse.builder()
            .message("Token generated successfully")
            .status("SUCCESS")
            .token(Collections.singletonList("mock-verification-token-123"))
            .build();
    }

    @Test
    void shouldRegisterCustomerSuccessfully() {
        // Mock service calls
        when(customerServiceClient.createCustomer(any(Map.class)))
            .thenReturn(Mono.just(customerResponse));
        when(customerServiceClient.generateVerificationToken(any()))
            .thenReturn(Mono.just(tokenResponse));
        when(customerServiceClient.sendVerificationEmail(anyString(), anyString(), anyString()))
            .thenReturn(Mono.empty());

        // Test the registration flow
        StepVerifier.create(registrationService.register(validRequest))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Registration successful") &&
                       response.getToken() != null &&
                       response.getToken().contains("mock-verification-token-123") &&
                       response.getCustomerDTO() != null;
            })
            .verifyComplete();
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        RegistrationRequest invalidRequest = new RegistrationRequest(
            "John", "Doe", "invalid-email", "password123",
            "555-0123", "123", "Main St", "Anytown", "VA", "Weekly", "12345"
        );

        StepVerifier.create(registrationService.register(invalidRequest))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().contains("Invalid email format"))
            .verify();
    }

    @Test
    void shouldRejectShortPassword() {
        RegistrationRequest shortPasswordRequest = new RegistrationRequest(
            "John", "Doe", "john.doe@example.com", "short",
            "555-0123", "123", "Main St", "Anytown", "VA", "Weekly", "12345"
        );

        StepVerifier.create(registrationService.register(shortPasswordRequest))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().contains("Password must be at least 8 characters"))
            .verify();
    }

    @Test
    void shouldHandleCustomerCreationFailure() {
        ApiResponse failedCustomerResponse = ApiResponse.builder()
            .message("Email already exists")
            .status("FAILED")
            .build();

        when(customerServiceClient.createCustomer(any(Map.class)))
            .thenReturn(Mono.just(failedCustomerResponse));

        StepVerifier.create(registrationService.register(validRequest))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Registration failed");
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleTokenGenerationFailure() {
        ApiResponse emptyTokenResponse = ApiResponse.builder()
            .message("Token generation failed")
            .status("SUCCESS")
            .token(Collections.emptyList())
            .build();

        when(customerServiceClient.createCustomer(any(Map.class)))
            .thenReturn(Mono.just(customerResponse));
        when(customerServiceClient.generateVerificationToken(any()))
            .thenReturn(Mono.just(emptyTokenResponse));

        StepVerifier.create(registrationService.register(validRequest))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Registration failed");
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleServiceUnavailableError() {
        when(customerServiceClient.createCustomer(any(Map.class)))
            .thenReturn(Mono.error(new RuntimeException("Customer service unavailable")));

        StepVerifier.create(registrationService.register(validRequest))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Customer service unavailable");
            })
            .verifyComplete();
    }

    @Test
    void shouldConfirmEmailSuccessfully() {
        ApiResponse confirmResponse = ApiResponse.builder()
            .message("Email confirmed successfully")
            .status("SUCCESS")
            .build();

        when(customerServiceClient.confirmEmailToken(anyString()))
            .thenReturn(Mono.just(confirmResponse));

        StepVerifier.create(registrationService.confirmEmail("valid-token"))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Email confirmed");
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleInvalidToken() {
        when(customerServiceClient.confirmEmailToken(anyString()))
            .thenReturn(Mono.error(new RuntimeException("Invalid token")));

        StepVerifier.create(registrationService.confirmEmail("invalid-token"))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Invalid or expired token");
            })
            .verifyComplete();
    }

    @Test
    void shouldCheckTokenStatusSuccessfully() {
        ApiResponse statusResponse = ApiResponse.builder()
            .message("Token is valid")
            .status("SUCCESS")
            .build();

        when(customerServiceClient.getTokenStatus(anyString()))
            .thenReturn(Mono.just(statusResponse));

        StepVerifier.create(registrationService.getTokenStatus("valid-token"))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Token is valid");
            })
            .verifyComplete();
    }

    @Test
    void shouldResendVerificationEmailSuccessfully() {
        ApiResponse resendResponse = ApiResponse.builder()
            .message("Verification email resent")
            .status("SUCCESS")
            .build();

        when(customerServiceClient.resendVerificationEmail(anyString()))
            .thenReturn(Mono.just(resendResponse));

        StepVerifier.create(registrationService.resendVerificationEmail("john.doe@example.com"))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Verification email resent");
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleResendVerificationFailure() {
        when(customerServiceClient.resendVerificationEmail(anyString()))
            .thenReturn(Mono.error(new RuntimeException("Email not found")));

        StepVerifier.create(registrationService.resendVerificationEmail("nonexistent@example.com"))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Failed to resend verification");
            })
            .verifyComplete();
    }

    @Test
    void shouldNormalizeEmailToLowerCase() {
        RegistrationRequest upperCaseEmailRequest = new RegistrationRequest(
            "John", "Doe", "JOHN.DOE@EXAMPLE.COM", "password123",
            "555-0123", "123", "Main St", "Anytown", "VA", "Weekly", "12345"
        );

        when(customerServiceClient.createCustomer(argThat(customerData -> {
            String email = (String) customerData.get("email");
            return "john.doe@example.com".equals(email);
        }))).thenReturn(Mono.just(customerResponse));
        
        when(customerServiceClient.generateVerificationToken(any()))
            .thenReturn(Mono.just(tokenResponse));
        when(customerServiceClient.sendVerificationEmail(anyString(), anyString(), anyString()))
            .thenReturn(Mono.empty());

        StepVerifier.create(registrationService.register(upperCaseEmailRequest))
            .expectNextMatches(response -> "SUCCESS".equals(response.getStatus()))
            .verifyComplete();
    }
}