package com.research.assistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(geminiApiUrl)   // ✅ BASE URL SET ONCE
                .build();
    }
}
