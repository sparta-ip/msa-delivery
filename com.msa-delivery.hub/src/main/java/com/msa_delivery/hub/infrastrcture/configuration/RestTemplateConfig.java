package com.msa_delivery.hub.infrastrcture.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean

    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
