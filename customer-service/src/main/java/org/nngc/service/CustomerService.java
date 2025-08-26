package org.nngc.service;

import org.nngc.dto.CustomerDTO;
import org.nngc.entity.Customer;
import org.nngc.response.ApiResponse;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    Customer addCustomer(Customer customer);
    Customer createCustomer(Map<String, Object> customerData);
    ApiResponse resendVerificationEmail(String email);
    void updateStripeForAllUsers();
    void addBulkCustomers(List<Customer> customers);
    List<Customer> getCustomers();
    List<Customer> findCustomersWithSorting(String field, String direction);
    void deleteCustomer(Long id);
    CustomerDTO updateCustomer(Customer customer, Long id);
    CustomerDTO updateCustomer(Customer customer, String email);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO getCustomerByStripeId(String id);
}
