package org.nngc.controller;

import org.nngc.dto.CustomerDTO;
import org.nngc.entity.Customer;
import org.nngc.response.ApiResponse;
import org.nngc.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        logger.info("Getting customer by id: {}", id);
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody Map<String, Object> customerData) {
        logger.info("Creating new customer with email: {}", customerData.get("email"));
        
        try {
            Customer customer = customerService.createCustomer(customerData);
            CustomerDTO customerDTO = customer.toCustomerDTO();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                    .message("Customer created successfully")
                    .customerDTO(customerDTO)
                    .status("SUCCESS")
                    .build()
            );
        } catch (Exception e) {
            logger.error("Failed to create customer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                    .message("Failed to create customer: " + e.getMessage())
                    .status("FAILED")
                    .build()
            );
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(@RequestParam String email) {
        logger.info("Processing resend verification for: {}", email);
        
        try {
            ApiResponse response = customerService.resendVerificationEmail(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to resend verification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                    .message("Failed to resend verification: " + e.getMessage())
                    .status("FAILED")
                    .build()
            );
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Service is running");
    }
}