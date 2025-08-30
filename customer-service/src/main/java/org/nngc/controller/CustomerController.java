package org.nngc.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.nngc.dto.CustomerDTO;
import org.nngc.entity.Customer;
import org.nngc.response.ApiResponse;
import org.nngc.response.RegistrationRequest;
import org.nngc.service.CustomerService;
import org.nngc.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
@Validated
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final String FAILED="FAILED";
    private final String email="email";
    private final CustomerService customerService;
    private final RegistrationService registrationService;

    public CustomerController(CustomerService customerService, RegistrationService registrationService) {
        this.customerService = customerService;
        this.registrationService = registrationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable @NotNull Long id) {
        logger.info("Getting customer by id: {}", id);
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody @Valid Map<String, Object> customerData) {
        logger.info("Creating new customer with email: {}", customerData.get(email));
        
        // Basic validation
        if (customerData.get(email) == null || customerData.get(email).toString().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                    .message("Email is required")
                    .status(FAILED)
                    .build()
            );
        }
        
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
        } catch (IllegalStateException | IllegalArgumentException e) {
            logger.error("Failed to create customer", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                    .message("Failed to create customer: " + e.getMessage())
                    .status(FAILED)
                    .build()
            );
        } catch (RuntimeException e) {
            logger.error("Unexpected error creating customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                    .message("Internal server error")
                    .status(FAILED)
                    .build()
            );
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(@RequestParam @NotBlank String email) {
        logger.info("Processing resend verification for: {}", email);
        
        try {
            ApiResponse response = customerService.resendVerificationEmail(email);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to resend verification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                    .message("Failed to resend verification: " + e.getMessage())
                    .status(FAILED)
                    .build()
            );
        } catch (RuntimeException e) {
            logger.error("Unexpected error during resend verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                    .message("Internal server error")
                    .status(FAILED)
                    .build()
            );
        }
    }
    
    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse>> registerCustomer(@RequestBody @Valid RegistrationRequest request) {
        logger.info("Processing registration for: {}", request.getEmail());
        
        return registrationService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> {
                    logger.error("Registration failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Registration failed: " + error.getMessage())
                            .status(FAILED)
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @PostMapping("/confirm-email")
    public Mono<ResponseEntity<ApiResponse>> confirmEmail(@RequestParam @NotBlank String token) {
        logger.info("Processing email confirmation");
        
        return registrationService.confirmEmail(token)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    logger.error("Email confirmation failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Invalid or expired token")
                            .status(FAILED)
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @GetMapping("/token-status")
    public Mono<ResponseEntity<ApiResponse>> getTokenStatus(@RequestParam @NotBlank String token) {
        logger.info("Processing token status check");
        
        return registrationService.getTokenStatus(token)
                .map(response -> {
                    String status = "invalid";
                    if ("SUCCESS".equals(response.getStatus()) || "ALREADY_CONFIRMED".equals(response.getStatus())) {
                        status = "good";
                    } else if ("EXPIRED".equals(response.getStatus())) {
                        status = "expired";
                    }
                    
                    ApiResponse tokenStatusResponse = ApiResponse.builder()
                            .message(status)
                            .status(response.getStatus())
                            .build();
                    
                    return ResponseEntity.ok(tokenStatusResponse);
                })
                .onErrorResume(error -> {
                    logger.error("Token status check failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("invalid")
                            .status(FAILED)
                            .build();
                    return Mono.just(ResponseEntity.ok(errorResponse));
                });
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Service is running");
    }
}