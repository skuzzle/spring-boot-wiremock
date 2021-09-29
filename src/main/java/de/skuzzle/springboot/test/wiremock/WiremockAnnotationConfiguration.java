package de.skuzzle.springboot.test.wiremock;

import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_HTTPS_PORT;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_HTTP_PORT;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_INJECT_HTTPS_HOST_INTO;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_INJECT_HTTP_HOST_INTO;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_KEYSTORE_LOCATION;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_KEYSTORE_PASSWORD;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_KEYSTORE_TYPE;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_NEED_CLIENT_AUTH;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_SSL_ONLY;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_TRUSTSTORE_LOCATION;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_TRUSTSTORE_PASSWORD;
import static de.skuzzle.springboot.test.wiremock.WithWiremock.PROP_TRUSTSTORE_TYPE;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.base.Preconditions;

/**
 * Reads the application properties configured from {@link WithWiremock} annotation and
 * creates a {@link WireMockConfiguration}.
 *
 * @author Simon Taddiken
 */
final class WiremockAnnotationConfiguration {

    private final ResourceLoader resourceLoader;
    private final Environment environment;

    private WiremockAnnotationConfiguration(ApplicationContext applicationContext) {
        Preconditions.checkArgument(applicationContext != null, "applicationContext must not be null");
        this.environment = applicationContext.getEnvironment();
        this.resourceLoader = applicationContext;
    }

    public static WiremockAnnotationConfiguration from(ApplicationContext applicationContext) {
        return new WiremockAnnotationConfiguration(applicationContext);
    }

    public String getInjectHttpHostPropertyName() {
        return getString(fromProperty(PROP_INJECT_HTTP_HOST_INTO));
    }

    public String getInjectHttpsHostPropertyName() {
        return getString(fromProperty(PROP_INJECT_HTTPS_HOST_INTO));
    }

    public boolean needsClientAuth() {
        return getBoolean(fromProperty(PROP_NEED_CLIENT_AUTH));
    }

    public int httpPort() {
        return getInt(fromProperty(PROP_HTTP_PORT));
    }

    public int httpsPort() {
        return getInt(fromProperty(PROP_HTTPS_PORT));
    }

    public boolean sslOnly() {
        return getBoolean(fromProperty(PROP_SSL_ONLY));
    }

    WireMockConfiguration createWiremockConfig() {
        final boolean needClientAuth = needsClientAuth();
        final boolean sslOnly = sslOnly();
        final int httpPort = httpPort();
        final int httpsPort = httpsPort();

        final String keystoreLocation = getResource(fromProperty(PROP_KEYSTORE_LOCATION));
        final String keystorePassword = getString(fromProperty(PROP_KEYSTORE_PASSWORD));
        final String keystoreType = getString(fromProperty(PROP_KEYSTORE_TYPE));

        final String truststoreLocation = getResource(fromProperty(PROP_TRUSTSTORE_LOCATION));
        final String truststorePassword = getString(fromProperty(PROP_TRUSTSTORE_PASSWORD));
        final String truststoreType = getString(fromProperty(PROP_TRUSTSTORE_TYPE));

        final WireMockConfiguration configuration = new WireMockConfiguration()
                .needClientAuth(needClientAuth)
                .httpDisabled(sslOnly)
                .port(httpPort)
                .httpsPort(httpsPort);
        if (keystoreLocation != null) {
            configuration
                    .keystorePath(keystoreLocation)
                    .keystorePassword(keystorePassword)
                    .keystoreType(keystoreType);
        }
        if (truststoreLocation != null) {
            configuration
                    .trustStorePath(truststoreLocation)
                    .trustStorePassword(truststorePassword)
                    .trustStoreType(truststoreType);
        }
        return configuration;
    }

    private String fromProperty(String prop) {
        return WithWiremock.PREFIX + "." + prop;
    }

    private int getInt(String key) {
        return environment.getProperty(key, Integer.class, 0);
    }

    private boolean getBoolean(String key) {
        return environment.getProperty(key, Boolean.class, false);
    }

    private String getString(String key) {
        return environment.getProperty(key, "");
    }

    private String getResource(String key) {
        final String location = environment.getProperty(key);
        if (location == null || location.isEmpty()) {
            return null;
        }
        final Resource resource = resourceLoader.getResource(location);
        try {
            return resource.getURL().toString();
        } catch (final IOException e) {
            throw new IllegalArgumentException("Error resolving location for property " + key, e);
        }
    }
}