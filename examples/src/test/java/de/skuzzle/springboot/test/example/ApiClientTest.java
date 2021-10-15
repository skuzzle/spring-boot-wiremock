package de.skuzzle.springboot.test.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.skuzzle.springboot.test.wiremock.WithWiremock;
import de.skuzzle.springboot.test.wiremock.stubs.Auth;
import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;
import de.skuzzle.springboot.test.wiremock.stubs.Request;
import de.skuzzle.springboot.test.wiremock.stubs.Response;

@SpringBootTest(properties = { "api.username=user", "api.password=pw" })
@WithWiremock(injectHttpHostInto = "api.baseUrl",
        withGlobalAuthentication = @Auth(basicAuthUsername = "user", basicAuthPassword = "pw"))
class ApiClientTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    @HttpStub(
            onRequest = @Request(withMethod = "GET", toUrlPath = "/"),
            respond = @Response(withBody = "results"))
    void testGetResultsFromBackend() {
        final String resultsFromBackend = apiClient.getResultsFromBackend();
        assertThat(resultsFromBackend).isEqualTo("results");
    }

}
