package org.nngc.registrationservice.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrations,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 = 
            new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrations,
                authorizedClientRepository
            );
        oauth2.setDefaultClientRegistrationId("keycloak");
        
        return WebClient.builder()
                .filter(oauth2);
    }
}