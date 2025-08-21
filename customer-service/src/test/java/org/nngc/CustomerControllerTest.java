package org.nngc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nngc.controller.CustomerController;
import org.nngc.entity.Customer;
import org.nngc.token.Token;
import org.nngc.token.TokenType;
import org.nngc.repository.TokenRepository;
import org.nngc.response.ApiResponse;
import org.nngc.roles.AppUserRoles;
import org.nngc.service.impl.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 @ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private TokenRepository tokenRepository;

    // Static test data - created once and reused
    private static Customer testCustomer;
    private static Customer testAdminCustomer;
    private static Customer testBusinessCustomer;
    private static Token testToken;
    private static Token testAdminToken;
    private static Token testBusinessToken;
    private static List<Customer> testCustomerList;

    @BeforeAll
    static void setUpTestData() {
        // Create test customers once for all tests
        testCustomer = createTestCustomer(1L, "john.doe@example.com", AppUserRoles.STRIPE_CUSTOMER);
        testAdminCustomer = createTestCustomer(2L, "admin@example.com", AppUserRoles.ADMIN);
        testBusinessCustomer = createTestCustomer(3L, "business@example.com", AppUserRoles.BUSINESS);

        // Create tokens for authorization testing
        testToken = createTestToken("test-token-123", testCustomer);
        testAdminToken = createTestToken("admin-token-456", testAdminCustomer);
        testBusinessToken = createTestToken("business-token-789", testBusinessCustomer);

        // Create a list of customers for bulk operations
        testCustomerList = Arrays.asList(testCustomer, testAdminCustomer, testBusinessCustomer);
    }

    @BeforeEach
    void setUp() {
        // Reset mocks before each test (clears previous interactions)
        reset(customerService, tokenRepository);

        // Set up common mock behaviors for each test
        setupCommonMocks();
    }

    /**
     * Creates a test customer with the specified parameters
     */
    private static Customer createTestCustomer(Long id, String email, AppUserRoles role) {
        return Customer.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .password("$2a$10$encodedPassword")
                .phone("555123456" + id) // Make phone unique
                .houseNumber("12" + id)
                .streetName("Main Street")
                .city("Richmond")
                .state("VA")
                .zipCode("23230")
                .county("Henrico")
                .latitude(37.5407 + id * 0.001) // Slight variation for each customer
                .longitude(-77.4360 + id * 0.001)
                .geoLocation((37.5407 + id * 0.001) + "," + (-77.4360 + id * 0.001))
                .service("Weekly Pickup")
                .stripeCustomerId("cus_test" + id)
                .receiptURL("https://example.com/receipt/" + id)
                .invoiceURL("https://example.com/invoice/" + id)
                .enabled(true)
                .changePassword(false)
                .appUserRoles(role)
                .build();
    }

    /**
     * Creates a test token for authorization
     */
    private static Token createTestToken(String tokenValue, Customer customer) {
        return Token.builder()
                .token(tokenValue)
                .tokenType(TokenType.BEARER)
                .customer(customer)
                .expired(false)
                .revoked(false)
                .build();
    }

    /**
     * Sets up common mock behaviors used across multiple tests
     */
    private void setupCommonMocks() {
        // Mock token repository responses
        when(tokenRepository.findByToken("test-token-123"))
                .thenReturn(Optional.of(testToken));
        when(tokenRepository.findByToken("admin-token-456"))
                .thenReturn(Optional.of(testAdminToken));
        when(tokenRepository.findByToken("business-token-789"))
                .thenReturn(Optional.of(testBusinessToken));
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());
    }

    // Test methods using the shared customer data

    @Test
    void testGetCustomerById_AsAdmin_ShouldReturnCustomer() throws Exception {
        // Arrange
        ApiResponse<Customer> expectedResponse = ApiResponse.<Customer>builder()
                .message("Customer found")
                .customerDTO(testCustomer.toCustomerDTO())
                .build();

        when(customerService.getCustomerById(1L)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/customer/customers/1")
                        .header("Authorization", "admin-token-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer found"))
                .andExpect(jsonPath("$.customerDTO.email").value("john.doe@example.com"));
    }

    @Test
    void testGetAllCustomers_AsAdmin_ShouldReturnCustomerList() throws Exception {
        // Arrange
        ApiResponse<List<Customer>> expectedResponse = ApiResponse.<List<Customer>>builder()
                .message("Customers retrieved successfully")
                .customers(testCustomerList)
                .build();

        when(customerService.getCustomers()).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/customer/customers")
                        .header("Authorization", "admin-token-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customers retrieved successfully"));
    }

    @Test
    void testUpdateCustomer_AsOwner_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        ApiResponse<Customer> expectedResponse = ApiResponse.<Customer>builder()
                .message("Customer updated successfully")
                .customerDTO(testCustomer.toCustomerDTO())
                .build();

        when(customerService.updateCustomer(any(Customer.class), eq(1L)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put("/customer/customers/1")
                        .header("Authorization", "test-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer updated successfully"));
    }

    // Utility methods for creating variations when needed

    /**
     * Create a new customer instance when you need to modify it for specific tests
     */
    public static Customer createCustomerCopy(Customer original) {
        return Customer.builder()
                .id(original.getId())
                .firstName(original.getFirstName())
                .lastName(original.getLastName())
                .email(original.getEmail())
                .password(original.getPassword())
                .phone(original.getPhone())
                .houseNumber(original.getHouseNumber())
                .streetName(original.getStreetName())
                .city(original.getCity())
                .state(original.getState())
                .zipCode(original.getZipCode())
                .county(original.getCounty())
                .latitude(original.getLatitude())
                .longitude(original.getLongitude())
                .geoLocation(original.getGeoLocation())
                .service(original.getService())
                .stripeCustomerId(original.getStripeCustomerId())
                .receiptURL(original.getReceiptURL())
                .invoiceURL(original.getInvoiceURL())
                .enabled(original.isEnabled())
                .changePassword(original.isChangePassword())
                .appUserRoles(original.getAppUserRoles())
                .build();
    }

    /**
     * Get different customer types for specific test scenarios
     */
    public static Customer getRegularCustomer() { return testCustomer; }
    public static Customer getAdminCustomer() { return testAdminCustomer; }
    public static Customer getBusinessCustomer() { return testBusinessCustomer; }
    public static List<Customer> getAllTestCustomers() { return testCustomerList; }
}