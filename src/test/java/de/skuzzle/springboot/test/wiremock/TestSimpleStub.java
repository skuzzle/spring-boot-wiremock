package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@WithWiremock(injectHttpHostInto = "mockHost")
public class TestSimpleStub {

    @Value("${mockHost}")
    private String mockHost;

    private RestTemplateBuilder client() {
        return new RestTemplateBuilder()
                .rootUri(mockHost);
    }

    @Test
    @HttpStub(
            onRequest = @Request(
                    withMethod = "POST",
                    toUrl = "/endpoint",
                    containingHeaders = "Request-Header=value"),
            authenticatedBy = @Auth(
                    basicAuthUsername = "username",
                    basicAuthPassword = "password"),
            respond = @Response(
                    withStatus = HttpStatus.CREATED,
                    withBody = "Hello World",
                    withContentType = "application/text",
                    withHeaders = "Response-Header=value"))
    void testSimpleStubWithBasicAuth_Body_ContentType_And_Headers() {
        final ResponseEntity<String> response = client()
                .basicAuthentication("username", "password")
                .defaultHeader("Request-Header", "value")
                .build()
                .postForEntity("/endpoint", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Hello World");
        assertThat(response.getHeaders().get("Content-Type")).containsOnly("application/text");
        assertThat(response.getHeaders().get("Response-Header")).containsOnly("value");
    }

    @Test
    @HttpStub(authenticatedBy = @Auth(bearerToken = "valid-token"))
    void testBearerAuth() {
        final RequestEntity<Void> requestEntity = RequestEntity.get("/")
                .header("Authorization", "bearer Valid-Token")
                .build();
        final ResponseEntity<String> response = client().build().exchange(requestEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @HttpStub(
            onRequest = @Request(
                    withMethod = "POST",
                    toUrl = "/endpoint"),
            respond = @Response(
                    withStatus = HttpStatus.CREATED,
                    withBody = "Hello World",
                    withContentType = "application/text",
                    withHeaders = "Content-Type=application/json"))
    void testContenTypeTakesPrecedenceOverHeaders() {
        final ResponseEntity<String> response = client().build().postForEntity("/endpoint", null, String.class);
        assertThat(response.getBody()).isEqualTo("Hello World");
        assertThat(response.getHeaders().get("Content-Type")).containsOnly("application/text");
    }

    @Test
    @HttpStub
    @HttpStub(onRequest = @Request(withMethod = "POST"), respond = @Response(withStatus = HttpStatus.CREATED))
    void testMultipleStubs() throws Exception {
        final ResponseEntity<String> responseGet = client().build().getForEntity("/", null, String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<String> responsePost = client().build().postForEntity("/", null, String.class);
        assertThat(responsePost.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
