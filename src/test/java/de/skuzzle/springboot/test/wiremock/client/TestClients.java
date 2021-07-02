package de.skuzzle.springboot.test.wiremock.client;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.function.Function;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public final class TestClients {

    public static ClientBuilder<RestTemplateBuilder, RestTemplate> restTemplate() {
        return new RestTemplateClientBuilder(new RestTemplateBuilder());
    }

    public static interface ClientBuilder<T, C> {
        ClientBuilder<T, C> customize(Function<? super T, T> builder);

        ClientBuilder<T, C> withBasicAuth(String username, String password);

        ClientBuilder<T, C> withBaseUrl(String baseUrl);

        ClientBuilder<T, C> withClientAuth(KeyStore keystore, char[] keyPassword);

        ClientBuilder<T, C> trusting(KeyStore truststore);

        C build();
    }

    public static final class RestTemplateClientBuilder implements ClientBuilder<RestTemplateBuilder, RestTemplate> {

        private RestTemplateBuilder builder;
        private final SSLContextBuilder sslContextBuilder;

        public RestTemplateClientBuilder(RestTemplateBuilder builder) {
            this.builder = builder;
            this.sslContextBuilder = SSLContextBuilder.create();
        }

        @Override
        public ClientBuilder<RestTemplateBuilder, RestTemplate> customize(
                Function<? super RestTemplateBuilder, RestTemplateBuilder> builder) {
            this.builder = builder.apply(this.builder);
            return this;
        }

        @Override
        public ClientBuilder<RestTemplateBuilder, RestTemplate> withBasicAuth(String username, String password) {
            builder = builder.basicAuthentication(username, password);
            return this;
        }

        @Override
        public ClientBuilder<RestTemplateBuilder, RestTemplate> withBaseUrl(String baseUrl) {
            builder = builder.rootUri(baseUrl);
            return this;
        }

        @Override
        public ClientBuilder<RestTemplateBuilder, RestTemplate> withClientAuth(KeyStore keystore, char[] keyPassword) {
            try {
                sslContextBuilder.loadKeyMaterial(keystore, keyPassword);
            } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
                throw new IllegalArgumentException("Error configuring keystore", e);
            }
            return this;
        }

        @Override
        public ClientBuilder<RestTemplateBuilder, RestTemplate> trusting(KeyStore truststore) {
            try {
                sslContextBuilder.loadTrustMaterial(truststore, null);
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                throw new IllegalArgumentException("Error configuring truststore", e);
            }

            return this;
        }

        @Override
        public RestTemplate build() {
            try {
                final CloseableHttpClient httpClient = HttpClientBuilder.create()
                        .setSSLContext(sslContextBuilder.build())
                        .build();

                final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
                return builder
                        .requestFactory(() -> requestFactory)
                        .build();
            } catch (final Exception e) {
                throw new IllegalArgumentException("Error configuring test client", e);
            }
        }
    }

}
