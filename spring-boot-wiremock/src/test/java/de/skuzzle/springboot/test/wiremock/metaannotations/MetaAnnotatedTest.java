package de.skuzzle.springboot.test.wiremock.metaannotations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.skuzzle.springboot.test.wiremock.client.TestClients;

@SpringBootTest
@WithSampleServiceMock
public class MetaAnnotatedTest {

    @Value("${sample-service.url}")
    private String sampleServiceUrl;

    private RestTemplate client() {
        return TestClients.restTemplate()
                .withBaseUrl(sampleServiceUrl)
                .withBasicAuth("user", "password")
                .build();
    }

    @Test
    void testGetInfo() throws Exception {
        final ResponseEntity<Object> entity = client().getForEntity("/info", Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
