package de.skuzzle.springboot.test.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest
@WithWiremock(injectHttpHostInto = "your.application.serviceUrl",
        injectHttpsHostInto = "your.application.serviceUrlSsl")
public class WiremockInitializerTest {

    @Value("${your.application.serviceUrl}")
    private String serviceUrl;
    @Value("${your.application.serviceUrlSsl}")
    private String serviceUrlSsl;

    @Autowired
    private WireMockServer wiremock;

    @Test
    void testCallWiremockWithRestTemplate() throws Exception {
        wiremock.stubFor(WireMock.get("/")
                .willReturn(aResponse().withStatus(200)));
        final ResponseEntity<Object> response = new RestTemplateBuilder()
                .rootUri(serviceUrl)
                .build()
                .getForEntity("/", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
