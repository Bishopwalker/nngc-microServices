package org.nngc.service.impl;

import org.nngc.dto.CustomerDTO;
import org.nngc.entity.Customer;
import org.nngc.repository.CustomerRepository;
import org.nngc.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void updateStripeForAllUsers() {
        System.out.println("Stripe update - not implemented yet");
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