package org.nngc.service.impl;

import org.nngc.dto.CustomerDTO;
import org.nngc.entity.Customer;
import org.nngc.repository.CustomerRepository;
import org.nngc.response.ApiResponse;
import org.nngc.roles.AppUserRoles;
import org.nngc.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    private final CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    @Override
    public Customer createCustomer(Map<String, Object> customerData) {
        log.info("Creating new customer with email: {}", customerData.get("email"));
        
        String email = (String) customerData.get("email");
        
        // Check if customer already exists
        Optional<Customer> existing = customerRepository.findByEmail(email.toLowerCase());
        if (existing.isPresent()) {
            throw new IllegalStateException("Customer with email " + email + " already exists");
        }
        
        // Create new customer
        Customer customer = Customer.builder()
                .firstName((String) customerData.get("firstName"))
                .lastName((String) customerData.get("lastName"))
                .email(email.toLowerCase())
                .password(passwordEncoder.encode((String) customerData.get("password")))
                .phone((String) customerData.get("phone"))
                .houseNumber((String) customerData.get("houseNumber"))
                .streetName((String) customerData.get("streetName"))
                .city((String) customerData.get("city"))
                .state((String) customerData.get("state"))
                .zipCode((String) customerData.get("zipCode"))
                .service((String) customerData.get("service"))
                .appUserRoles(AppUserRoles.USER)
                .enabled((Boolean) customerData.getOrDefault("enabled", false))
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        
        return savedCustomer;
    }
    
    @Override
    public ApiResponse resendVerificationEmail(String email) {
        log.info("Processing resend verification for: {}", email);
        
        Optional<Customer> customerOpt = customerRepository.findByEmail(email.toLowerCase());
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with email: " + email);
        }
        
        Customer customer = customerOpt.get();
        if (customer.isEnabled()) {
            return ApiResponse.builder()
                    .message("Account is already verified")
                    .status("ALREADY_VERIFIED")
                    .build();
        }
        
        // This would typically trigger token generation and email sending
        // For now, just return success response
        return ApiResponse.builder()
                .message("Verification email resend request processed")
                .customerDTO(customer.toCustomerDTO())
                .status("SUCCESS")
                .build();
    }

    @Override
    public void updateStripeForAllUsers() {
        log.info("Stripe update - not implemented yet");
    }

    @Override
    public void addBulkCustomers(List<Customer> customers) {
        customerRepository.saveAll(customers);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> findCustomersWithSorting(String field, String direction) {
        return customerRepository.findAll();
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerDTO updateCustomer(Customer customer, Long id) {
        var existingCustomer = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        
        // Update fields
        if (customer.getFirstName() != null) existingCustomer.setFirstName(customer.getFirstName());
        if (customer.getLastName() != null) existingCustomer.setLastName(customer.getLastName());
        if (customer.getEmail() != null) existingCustomer.setEmail(customer.getEmail());
        if (customer.getPhone() != null) existingCustomer.setPhone(customer.getPhone());
        
        var updated = customerRepository.save(existingCustomer);
        return updated.toCustomerDTO();
    }

    @Override
    public CustomerDTO updateCustomer(Customer customer, String email) {
        var existingCustomer = customerRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        
        // Update password if provided
        if (customer.getPassword() != null) {
            existingCustomer.setPassword(customer.getPassword());
        }
        
        var updated = customerRepository.save(existingCustomer);
        return updated.toCustomerDTO();
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        var customer = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        return customer.toCustomerDTO();
    }

    @Override
    public CustomerDTO getCustomerByStripeId(String id) {
        var customer = customerRepository.locateByStripeID(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        return customer.toCustomerDTO();
    }
}