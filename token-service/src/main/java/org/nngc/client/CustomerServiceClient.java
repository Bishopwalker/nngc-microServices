package org.nngc.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class CustomerServiceClient {

    private WebClient webClient;

    public Customer getCustomerById(Long customerId) {
        return webClient.get()
                .uri()
    }
}
