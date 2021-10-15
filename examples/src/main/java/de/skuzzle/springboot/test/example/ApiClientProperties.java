package de.skuzzle.springboot.test.example;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("api")
class ApiClientProperties {

    private final String baseUrl;
    private final String username;
    private final String password;

    ApiClientProperties(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    public String baseUrl() {
        return this.baseUrl;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }
}
