package de.skuzzle.springboot.test.wiremock;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(WiremockAnnotationConfiguration.class);

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

    private int httpPort() {
        // NOTE: for HTTP (in contrast to HTTPS), the fixed port takes precedence over
        // random port. (Otherwise one would need to specify both randomHttpPort = false
        // AND fixedHttpPort = 1337 to use a fixed port (because randomHttpPort defaults
        // to true)
        if (wwm.fixedHttpPort() != WithWiremock.DEFAULT_HTTP_PORT) {
            return wwm.fixedHttpPort();
        }
        if (wwm.randomHttpPort()) {
            Preconditions.checkArgument(wwm.fixedHttpPort() == 0,
                    "Inconsistent HTTP port configuration. Either configure 'randomHttpPort' OR 'fixedHttpPort'");
            Preconditions.checkArgument(wwm.httpPort() == 0,
                    "Inconsistent HTTP port configuration. Either configure 'randomHttpPort' OR 'fixedHttpPort'");
            return 0;
        }
        if (wwm.httpPort() != WithWiremock.DEFAULT_HTTP_PORT) {
            return wwm.httpPort();
        }
        return wwm.fixedHttpPort();
    }

    private int httpsPort() {
        if (wwm.randomHttpsPort()) {
            Preconditions.checkArgument(wwm.fixedHttpsPort() == WithWiremock.DEFAULT_HTTPS_PORT,
                    "Inconsistent HTTPS port configuration. Either configure 'randomHttpsPort' OR 'fixedHttpsPort'");
            Preconditions.checkArgument(wwm.httpsPort() == WithWiremock.DEFAULT_HTTPS_PORT,
                    "Inconsistent HTTPS port configuration. Either configure 'randomHttpsPort' OR 'fixedHttpsPort'");
            return 0;
        }
        if (wwm.httpsPort() != WithWiremock.DEFAULT_HTTPS_PORT) {
            Preconditions.checkArgument(wwm.fixedHttpsPort() == WithWiremock.DEFAULT_HTTPS_PORT,
                    "Inconsistent HTTPS port configuration: Deprecated and new port attribute specified. Please use 'fixedHttpsPort' instead");
            return wwm.httpsPort();
        }
        return wwm.fixedHttpsPort();
    }

    public WireMockServer createWireMockServer() {
        return new WireMockServer(createWiremockConfig());
    }

    private WireMockConfiguration createWiremockConfig() {
        final boolean needClientAuth = wwm.needClientAuth();
        final boolean sslOnly = wwm.sslOnly();
        final int httpPort = httpPort();
        log.debug("Determined {} as HTTP port from {}", httpPort, wwm);
        final int httpsPort = httpsPort();
        log.debug("Determined {} as HTTPS port from {}", httpsPort, wwm);

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
