package de.skuzzle.springboot.test.example;

import org.springframework.web.client.RestTemplate;

public class ApiClient {

    private final RestTemplate resttemplate;

    public ApiClient(RestTemplate resttemplate) {
        this.resttemplate = resttemplate;
    }

    public String getResultsFromBackend() {
        return resttemplate.getForObject("/", String.class);
    }

}
