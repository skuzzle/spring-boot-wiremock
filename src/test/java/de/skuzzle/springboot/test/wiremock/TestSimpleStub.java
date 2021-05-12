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
    @SimpleStub(
            request = @Request(
                    method = "POST",
                    url = "/endpoint",
                    headers = "Request-Header=value"),
            response = @Response(
                    status = 201,
                    body = "Hello World",
                    contentType = "application/text",
                    headers = "Response-Header=value"),
            auth = @Auth(
                    basicAuthUsername = "username",
                    basicAuthPassword = "password"))
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
    @SimpleStub(auth = @Auth(bearerToken = "valid-token"))
    void testBearerAuth() {
        final RequestEntity<Void> requestEntity = RequestEntity.get("/")
                .header("Authorization", "bearer Valid-Token")
                .build();
        final ResponseEntity<String> response = client().build().exchange(requestEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @SimpleStub(
            request = @Request(
                    method = "POST",
                    url = "/endpoint"),
            response = @Response(
                    status = 201,
                    body = "Hello World",
                    contentType = "application/text",
                    headers = "Content-Type=application/json"))
    void testContenTypeTakesPrecedenceOverHeaders() {
        final ResponseEntity<String> response = client().build().postForEntity("/endpoint", null, String.class);
        assertThat(response.getBody()).isEqualTo("Hello World");
        assertThat(response.getHeaders().get("Content-Type")).containsOnly("application/text");
    }

    @Test
    @SimpleStub
    @SimpleStub(request = @Request(method = "POST"), response = @Response(status = 201))
    void testMultipleStubs() throws Exception {
        final ResponseEntity<String> responseGet = client().build().getForEntity("/", null, String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<String> responsePost = client().build().postForEntity("/", null, String.class);
        assertThat(responsePost.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
