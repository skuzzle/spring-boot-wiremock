package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@HttpStub
@WithWiremock(injectHttpHostInto = "serviceUrl")
public class WireMockInitializerHttpTest {

    @Value("${serviceUrl}")
    private String serviceUrl;

    private RestTemplateBuilder client() {
        return new RestTemplateBuilder()
                .rootUri(serviceUrl);
    }

    @Test
    void testCallWiremockWithRestTemplate() throws Exception {
        final ResponseEntity<String> response = client()
                .build()
                .getForEntity("/", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
