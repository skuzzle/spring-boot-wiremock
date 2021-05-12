package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SpringBootTest
@WithWiremock(injectHttpsHostInto = "serviceUrl", httpsPort = 0, sslOnly = true)
public class WireMockInitializerHttpsTest {

    @Value("${serviceUrl}")
    private String serviceUrl;

    private RestTemplateBuilder client() {
        try {
            final SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(TestKeystores.TEST_SERVER_CERTIFICATE_TRUST.getKeystore(), null)
                    .build();
            final CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setSSLContext(sslContext)
                    .build();

            final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            return new RestTemplateBuilder()
                    .requestFactory(() -> requestFactory)
                    .rootUri(serviceUrl);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    @SimpleStub
    void testCallWiremockWithRestTemplate() throws Exception {
        final ResponseEntity<String> response = client()
                .build()
                .getForEntity("/", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
