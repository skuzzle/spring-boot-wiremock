package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@WithWiremock(injectHttpHostInto = "serviceUrl")
public class HelloWorldTest {

    @Value("${serviceUrl}")
    private String serviceUrl;

    @Test
    @HttpStub(
            onRequest = @Request(withMethod = "POST"),
            respond = @Response(
                    withStatus = HttpStatus.CREATED,
                    withBody = "{\"value\": \"Hello World\"}",
                    withContentType = "application/json"))
    void testCallWiremockWithRestTemplate() throws Exception {
        final var response = new RestTemplate().postForEntity(serviceUrl, null, HelloWorld.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getValue()).isEqualTo("Hello World");
    }

    static class HelloWorld {
        private String value;

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
