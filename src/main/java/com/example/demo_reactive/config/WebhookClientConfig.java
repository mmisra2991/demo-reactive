package com.example.demo_reactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebhookClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}