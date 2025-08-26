package org.nngc.registrationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerServiceClientTest {

    private MockWebServer mockWebServer;
    private CustomerServiceClient customerServiceClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        objectMapper = new ObjectMapper();
        customerServiceClient = new CustomerServiceClient();
        
        // Use reflection to set the WebClient.Builder
        try {
            var field = CustomerServiceClient.class.getDeclaredField("webClientBuilder");
            field.setAccessible(true);
            field.set(customerServiceClient, WebClient.builder());
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup test", e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        // Setup mock response
        ApiResponse expectedResponse = ApiResponse.builder()
            .message("Customer created successfully")
            .status("SUCCESS")
            .customerDTO(createMockCustomerDTO())
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(objectMapper.writeValueAsString(expectedResponse))
            .addHeader("Content-Type", "application/json"));

        // Prepare test data
        Map<String, Object> customerData = createTestCustomerData();

        // Test the method
        StepVerifier.create(customerServiceClient.createCustomer(customerData))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Customer created successfully") &&
                       response.getCustomerDTO() != null;
            })
            .verifyComplete();

        // Verify request was made correctly
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/customers/create", request.getPath());
    }

    @Test
    void shouldHandleCustomerCreationError() throws Exception {
        // Setup error response
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(400)
            .setBody("{\"message\":\"Email already exists\"}")
            .addHeader("Content-Type", "application/json"));

        Map<String, Object> customerData = createTestCustomerData();

        StepVerifier.create(customerServiceClient.createCustomer(customerData))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Failed to create customer");
            })
            .verifyComplete();
    }

    @Test
    void shouldGenerateVerificationTokenSuccessfully() throws Exception {
        // Setup mock response
        ApiResponse expectedResponse = ApiResponse.builder()
            .message("Token generated successfully")
            .status("SUCCESS")
            .token(Collections.singletonList("mock-token-123"))
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(objectMapper.writeValueAsString(expectedResponse))
            .addHeader("Content-Type", "application/json"));

        Object customerDTO = createMockCustomerDTO();

        StepVerifier.create(customerServiceClient.generateVerificationToken(customerDTO))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getToken() != null &&
                       response.getToken().contains("mock-token-123");
            })
            .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/tokens/generate", request.getPath());
    }

    @Test
    void shouldSendVerificationEmailSuccessfully() throws Exception {
        // Setup mock response
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200));

        StepVerifier.create(customerServiceClient.sendVerificationEmail(
            "john@example.com", 
            "John", 
            "http://example.com/confirm?token=123"))
            .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/email/send-verification", request.getPath());
    }

    @Test
    void shouldConfirmEmailTokenSuccessfully() throws Exception {
        // Setup mock response
        ApiResponse expectedResponse = ApiResponse.builder()
            .message("Email confirmed successfully")
            .status("SUCCESS")
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(objectMapper.writeValueAsString(expectedResponse))
            .addHeader("Content-Type", "application/json"));

        StepVerifier.create(customerServiceClient.confirmEmailToken("valid-token"))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Email confirmed");
            })
            .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/tokens/confirm?token=valid-token", request.getPath());
    }

    @Test
    void shouldHandleTokenConfirmationError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(400)
            .setBody("{\"message\":\"Invalid token\"}")
            .addHeader("Content-Type", "application/json"));

        StepVerifier.create(customerServiceClient.confirmEmailToken("invalid-token"))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Invalid or expired token");
            })
            .verifyComplete();
    }

    @Test
    void shouldResendVerificationEmailSuccessfully() throws Exception {
        // Setup mock response
        ApiResponse expectedResponse = ApiResponse.builder()
            .message("Verification email resent")
            .status("SUCCESS")
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(objectMapper.writeValueAsString(expectedResponse))
            .addHeader("Content-Type", "application/json"));

        StepVerifier.create(customerServiceClient.resendVerificationEmail("john@example.com"))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Verification email resent");
            })
            .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/customers/resend-verification?email=john@example.com", request.getPath());
    }

    @Test
    void shouldGetTokenStatusSuccessfully() throws Exception {
        // Setup mock response
        ApiResponse expectedResponse = ApiResponse.builder()
            .message("Token is valid")
            .status("SUCCESS")
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(objectMapper.writeValueAsString(expectedResponse))
            .addHeader("Content-Type", "application/json"));

        StepVerifier.create(customerServiceClient.getTokenStatus("valid-token"))
            .expectNextMatches(response -> {
                return "SUCCESS".equals(response.getStatus()) &&
                       response.getMessage().contains("Token is valid");
            })
            .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/tokens/status?token=valid-token", request.getPath());
    }

    @Test
    void shouldHandleServiceTimeout() throws Exception {
        // Simulate timeout by not responding
        mockWebServer.enqueue(new MockResponse()
            .setBodyDelay(35, java.util.concurrent.TimeUnit.SECONDS));

        Map<String, Object> customerData = createTestCustomerData();

        StepVerifier.create(customerServiceClient.createCustomer(customerData))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Service unavailable");
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleNetworkError() throws Exception {
        // Simulate network error by closing server
        mockWebServer.shutdown();

        Map<String, Object> customerData = createTestCustomerData();

        StepVerifier.create(customerServiceClient.createCustomer(customerData))
            .expectNextMatches(response -> {
                return "FAILED".equals(response.getStatus()) &&
                       response.getMessage().contains("Customer service unavailable");
            })
            .verifyComplete();
    }

    private Map<String, Object> createTestCustomerData() {
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("firstName", "John");
        customerData.put("lastName", "Doe");
        customerData.put("email", "john.doe@example.com");
        customerData.put("password", "password123");
        customerData.put("phone", "555-0123");
        customerData.put("houseNumber", "123");
        customerData.put("streetName", "Main St");
        customerData.put("city", "Anytown");
        customerData.put("state", "VA");
        customerData.put("zipCode", "12345");
        customerData.put("service", "Weekly");
        customerData.put("enabled", false);
        return customerData;
    }

    private Object createMockCustomerDTO() {
        Map<String, Object> customerDTO = new HashMap<>();
        customerDTO.put("id", 1L);
        customerDTO.put("email", "john.doe@example.com");
        customerDTO.put("firstName", "John");
        customerDTO.put("lastName", "Doe");
        customerDTO.put("enabled", false);
        return customerDTO;
    }
}