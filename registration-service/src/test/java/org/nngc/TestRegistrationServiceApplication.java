package org.nngc;

import org.nngc.client.WebClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        WebClientConfig.class,
        org.nngc.client.CustomerServiceClient.class
    })
})
public class TestRegistrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestRegistrationServiceApplication.class, args);
    }
}