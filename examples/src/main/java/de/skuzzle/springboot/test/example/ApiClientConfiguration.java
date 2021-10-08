package de.skuzzle.springboot.test.example;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(ApiClientProperties.class)
class ApiClientConfiguration {

    @Bean
    public ApiClient apiClient(ApiClientProperties properties) {
        final RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(properties.baseUrl())
                .basicAuthentication(properties.username(), properties.password())
                .build();
        return new ApiClient(restTemplate);
    }
}
