package org.nngc.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nngc.dto.CustomerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerDTO getCustomerById(Long customerId) {
        try {
            return webClient.get()
                    .uri("http://customer-service/customer/{id}", customerId)
                    .retrieve()
                    .bodyToMono(CustomerDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error calling customer service for customer ID: {}, Error: {}", customerId, e.getMessage());
            throw new RuntimeException("Failed to fetch customer with ID: " + customerId, e);
        }
    }

    public CustomerDTO enableCustomer(Long customerId) {
        try {
            return webClient.put()
                    .uri("http://customer-service/customer/{id}/enable", customerId)
                    .retrieve()
                    .bodyToMono(CustomerDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error enabling customer with ID: {}, Error: {}", customerId, e.getMessage());
            throw new RuntimeException("Failed to enable customer with ID: " + customerId, e);
        }
    }
}


