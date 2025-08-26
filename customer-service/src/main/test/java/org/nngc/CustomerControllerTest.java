package org.nngc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nngc.entity.Customer;
import org.nngc.entity.controller.CustomerController;
import org.nngc.response.ApiResponse;
import org.nngc.roles.AppUserRoles;
import org.nngc.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;


    // Static test data - created once and reused
    private static Customer testCustomer;
    private static Customer testAdminCustomer;
    private static Customer testBusinessCustomer;
 static List<Customer> testCustomerList;

    @BeforeAll
    static void setUpTestData() {
        // Create test customers once for all tests
        testCustomer = createTestCustomer(1L, "john.doe@example.com", AppUserRoles.STRIPE_CUSTOMER);
        testAdminCustomer = createTestCustomer(2L, "admin@example.com", AppUserRoles.ADMIN);
        testBusinessCustomer = createTestCustomer(3L, "business@example.com", AppUserRoles.BUSINESS);



        // Create a list of customers for bulk operations
        testCustomerList = Arrays.asList(testCustomer, testAdminCustomer, testBusinessCustomer);
    }

    @BeforeEach
    void setUp() {
        // Reset mocks before each test (clears previous interactions)
        reset(customerService);

        // Set up common mock behaviors for each test
    }
    /**
     * Creates a test customerId with the specified parameters
     */
    private static Customer createTestCustomer(Long id, String email, AppUserRoles role) {
        return Customer.builder()
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
                .service("Weekly Pickup")
                .enabled(true)
                .appUserRoles(role)
                .build();
    }

    /**
     * Creates a test token for authorization
     */

    // Test methods using the shared customerId data

    @Test
    void testGetCustomerById_AsAdmin_ShouldReturnCustomer() throws Exception {
        // Arrange
        ApiResponse expectedResponse = ApiResponse.<Customer>builder()
                .message("Customer found")
                .customerDTO(testCustomer.toCustomerDTO())
                .build();

        when(customerService.getCustomerById(1L)).thenReturn(expectedResponse.getCustomerDTO());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/customerId/customers/1")
                        .header("Authorization", "admin-token-456"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Customer found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDTO.email").value("john.doe@example.com"));
    }

    @Test
    void testGetAllCustomers_AsAdmin_ShouldReturnCustomerList() throws Exception {
        // Arrange
        ApiResponse expectedResponse = ApiResponse.<List<Customer>>builder()
                .message("Customers retrieved successfully")
                .build();

        when(customerService.getCustomers()).thenReturn((List<Customer>) expectedResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/customerId/customers")
                        .header("Authorization", "admin-token-456"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Customers retrieved successfully"));
    }

    @Test
    void testUpdateCustomer_AsOwner_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        ApiResponse expectedResponse = ApiResponse.<Customer>builder()
                .message("Customer updated successfully")
                .customerDTO(testCustomer.toCustomerDTO())
                .build();

        when(customerService.updateCustomer(any(Customer.class), eq(1L)))
                .thenReturn(expectedResponse.getCustomerDTO());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/customerId/customers/1")
                        .header("Authorization", "test-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Customer updated successfully"));
    }

    // Utility methods for creating variations when needed

    /**
     * Create a new customerId instance when you need to modify it for specific tests
     */
    public static Customer createCustomerCopy(Customer original) {
        return Customer.builder()
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
                .service(original.getService())
                .enabled(original.isEnabled())
                .appUserRoles(original.getAppUserRoles())
                .build();
    }

    /**
     * Get different customerId types for specific test scenarios
     */
    public static Customer getRegularCustomer() { return testCustomer; }
    public static Customer getAdminCustomer() { return testAdminCustomer; }
    public static Customer getBusinessCustomer() { return testBusinessCustomer; }
    public static List<Customer> getAllTestCustomers() { return testCustomerList; }
}