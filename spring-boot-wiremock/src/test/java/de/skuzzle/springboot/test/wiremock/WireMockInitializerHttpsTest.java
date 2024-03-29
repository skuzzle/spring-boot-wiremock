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
@WithWiremock(injectHttpsHostInto = "serviceUrl", randomHttpsPort = true, sslOnly = true)
public class WireMockInitializerHttpsTest {

    @Value("${serviceUrl}")
    private String serviceUrl;

    private RestTemplate client() {
        return TestClients.restTemplate()
                .trusting(TestKeystores.TEST_SERVER_CERTIFICATE_TRUST.getKeystore())
                .withBaseUrl(serviceUrl)
                .build();
    }

    @Test
    @HttpStub
    void testCallWiremockWithRestTemplate() throws Exception {
        final ResponseEntity<String> response = client()
                .getForEntity("/", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
