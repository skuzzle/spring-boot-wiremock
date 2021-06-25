package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.skuzzle.springboot.test.wiremock.TestClients.ClientBuilder;

@SpringBootTest
@TestStubCollectionAnnotation
@WithWiremock(injectHttpHostInto = "mockHost")
public class TestHttpStub implements TestStubCollectionInterface {

    @Value("${mockHost}")
    private String mockHost;

    private ClientBuilder<RestTemplateBuilder, RestTemplate> client() {
        return TestClients.restTemplate()
                .withBaseUrl(mockHost);
    }

    private URI url(String path) {
        return URI.create(mockHost + path);
    }

    @Test
    @HttpStub(respond = @Response(withStatus = HttpStatus.CREATED))
    void testMatchAnyUrl() throws Exception {
        final ResponseEntity<Object> response = client().build().getForEntity(url("/whatever"), Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @HttpStub(
            onRequest = @Request(
                    withMethod = "POST",
                    toUrlPath = "/endpoint",
                    withQueryParameters = "param=matching:[a-z]+",
                    containingHeaders = "Request-Header=eq:value",
                    containingCookies = "sessionId=containing:123456",
                    withBody = "containing:Just a body",
                    authenticatedBy = @Auth(
                            basicAuthUsername = "username",
                            basicAuthPassword = "password")),
            respond = @Response(
                    withStatus = HttpStatus.CREATED,
                    withBody = "Hello World",
                    withContentType = "application/text",
                    withHeaders = "Response-Header=value"))
    void testSimpleStubWithBasicAuth_Body_ContentType_Cookie_QueryParam_And_Headers() {
        final RequestEntity<String> request = RequestEntity.post(url("/endpoint?param=abc"))
                .header("Request-Header", "value")
                .header("Cookie", "sessionId=1234567890")
                .body("Just a body");
        final ResponseEntity<String> response = client()
                .withBasicAuth("username", "password")
                .build()
                .exchange(request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Hello World");
        assertThat(response.getHeaders().get("Content-Type")).containsOnly("application/text");
        assertThat(response.getHeaders().get("Response-Header")).containsOnly("value");
    }

    @Test
    @HttpStub(onRequest = @Request(authenticatedBy = @Auth(bearerToken = "valid-token")))
    void testBearerAuth() {
        final RequestEntity<Void> requestEntity = RequestEntity.get(url("/"))
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
        final ResponseEntity<String> response = client().build().postForEntity(url("/endpoint"), null, String.class);
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

    @Test
    @HttpStub(onRequest = @Request(scenario = @Scenario(name = "Scenario", nextState = "1")))
    @HttpStub(onRequest = @Request(scenario = @Scenario(name = "Scenario", state = "1", nextState = "2")),
            respond = @Response(withStatus = HttpStatus.CREATED))
    @HttpStub(onRequest = @Request(scenario = @Scenario(name = "Scenario", state = "2", nextState = "1")),
            respond = @Response(withStatus = HttpStatus.OK))
    void testScenario() throws Exception {
        final ResponseEntity<String> response1 = client().build().getForEntity(url("/"), String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        final ResponseEntity<String> response2 = client().build().getForEntity(url("/"), String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        final ResponseEntity<String> response3 = client().build().getForEntity(url("/"), String.class);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
        final ResponseEntity<String> response4 = client().build().getForEntity(url("/"), String.class);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @HttpStub(respond = @Response(withBodyBase64 = "SGVsbG8gV29ybGQ=", withContentType = "text/plain"))
    void testWithBodyBase64() throws Exception {
        final ResponseEntity<String> response = client().build().getForEntity(url("/"), String.class);
        assertThat(response.getBody()).isEqualTo("Hello World");
    }

    @Test
    @HttpStub(respond = @Response(withBodyFile = "bodyFile.txt", withContentType = "text/plain"))
    void testWithBodyFromFile() throws Exception {
        final ResponseEntity<String> response = client().build().getForEntity(url("/"), String.class);
        assertThat(response.getBody()).isEqualTo("Hello World");
    }

    @Test
    @HttpStub(
            wrapAround = true,
            onRequest = @Request,
            respond = {
                    @Response(withStatus = HttpStatus.CREATED),
                    @Response(withStatus = HttpStatus.OK),
                    @Response(withStatus = HttpStatus.ACCEPTED)
            })
    void testConsecutiveWithtWrapAround() throws Exception {
        final ResponseEntity<String> response1 = client().build().getForEntity(url("/"), String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final ResponseEntity<String> response2 = client().build().getForEntity(url("/"), String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<String> response3 = client().build().getForEntity(url("/"), String.class);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        final ResponseEntity<String> response4 = client().build().getForEntity(url("/"), String.class);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @HttpStub(
            onRequest = @Request,
            respond = {
                    @Response(withStatus = HttpStatus.CREATED),
                    @Response(withStatus = HttpStatus.OK),
                    @Response(withStatus = HttpStatus.ACCEPTED)
            })
    void testConsecutiveWithoutWrapAround() throws Exception {
        final ResponseEntity<String> response1 = client().build().getForEntity(url("/"), String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final ResponseEntity<String> response2 = client().build().getForEntity(url("/"), String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<String> response3 = client().build().getForEntity(url("/"), String.class);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        assertThatExceptionOfType(HttpClientErrorException.class)
                .isThrownBy(() -> client().build().getForEntity(url("/"), String.class));
    }

    @Test
    void testStubsInheritedFromInterface() throws Exception {
        final ResponseEntity<String> response1 = client().build().getForEntity(url("/fromInterfaceCollection1"),
                String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        final ResponseEntity<String> response2 = client().build().getForEntity(url("/fromInterfaceCollection2"),
                String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testStubsInheritedFromMetaAnnotation() throws Exception {
        final ResponseEntity<String> response1 = client().build().getForEntity(url("/fromAnnotationCollection1"),
                String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        final ResponseEntity<String> response2 = client().build().getForEntity(url("/fromAnnotationCollection2"),
                String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
