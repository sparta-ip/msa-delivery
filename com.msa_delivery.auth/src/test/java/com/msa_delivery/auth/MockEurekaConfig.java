package com.msa_delivery.auth;

import com.netflix.discovery.EurekaClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockEurekaConfig {
    @Bean
    public EurekaClient eurekaClient() {
        return mock(EurekaClient.class);
    }

    @Bean(name = "mockDiscoveryClient")
    public DiscoveryClient discoveryClient() {
        return mock(DiscoveryClient.class);
    }
}
