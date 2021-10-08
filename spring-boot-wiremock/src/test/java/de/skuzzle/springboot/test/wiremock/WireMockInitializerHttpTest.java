package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.skuzzle.springboot.test.wiremock.client.TestClients;
import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;

@SpringBootTest
@HttpStub
@WithWiremock(injectHttpHostInto = "serviceUrl")
public class WireMockInitializerHttpTest {

    @Value("${serviceUrl}")
    private String serviceUrl;

    private RestTemplate client() {
        return TestClients.restTemplate()
                .withBaseUrl(serviceUrl)
                .build();
    }

    @Test
    void testCallWiremockWithRestTemplate() throws Exception {
        final ResponseEntity<String> response = client()
                .getForEntity("/", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
