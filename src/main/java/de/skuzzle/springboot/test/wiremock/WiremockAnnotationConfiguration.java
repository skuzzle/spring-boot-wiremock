package de.skuzzle.springboot.test.wiremock;

import java.io.IOException;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.base.Preconditions;

/**
 * Creates the {@link WireMockServer} from the values configured in {@link WithWiremock}
 * annotation.
 *
 * @author Simon Taddiken
 */
final class WiremockAnnotationConfiguration {

    private final WithWiremock wwm;
    private final ResourceLoader resourceLoader;

    private WiremockAnnotationConfiguration(WithWiremock wwm, ApplicationContext applicationContext) {
        Preconditions.checkArgument(wwm != null, "WithWiremock annotation must not be null");
        Preconditions.checkArgument(applicationContext != null, "applicationContext annotation must not be null");
        this.resourceLoader = applicationContext;
        this.wwm = wwm;
    }

    public static WiremockAnnotationConfiguration from(WithWiremock wwm, ApplicationContext applicationContext) {
        return new WiremockAnnotationConfiguration(wwm, applicationContext);
    }

    public WithWiremock withWiremockAnnotation() {
        return this.wwm;
    }

    public Set<String> getInjectHttpHostPropertyName() {
        return Set.of(wwm.injectHttpHostInto());
    }

    public Set<String> getInjectHttpsHostPropertyName() {
        return Set.of(wwm.injectHttpsHostInto());
    }

    public boolean sslOnly() {
        return wwm.sslOnly();
    }

    public WireMockServer createWireMockServer() {
        return new WireMockServer(createWiremockConfig());
    }

    private WireMockConfiguration createWiremockConfig() {
        final boolean needClientAuth = wwm.needClientAuth();
        final boolean sslOnly = wwm.sslOnly();
        final int httpPort = wwm.httpPort();
        final int httpsPort = wwm.httpsPort();

        final String keystoreLocation = getResource(wwm.keystoreLocation());
        final String keystorePassword = wwm.keystorePassword();
        final String keystoreType = wwm.keystoreType();

        final String truststoreLocation = getResource(wwm.truststoreLocation());
        final String truststorePassword = wwm.truststorePassword();
        final String truststoreType = wwm.truststoreType();

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

    private String getResource(String location) {
        if (location.isEmpty()) {
            return null;
        }
        final Resource resource = resourceLoader.getResource(location);
        try {
            return resource.getURL().toString();
        } catch (final IOException e) {
            throw new IllegalArgumentException("Error resolving resource for location " + location, e);
        }
    }
}
