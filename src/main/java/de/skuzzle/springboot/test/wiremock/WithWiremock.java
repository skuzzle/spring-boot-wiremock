package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.properties.PropertyMapping;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.skuzzle.springboot.test.wiremock.stubs.Auth;
import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;

/**
 * Configures a WireMock server that is integrated with the Spring ApplicationContext. Use
 * in conjunction with any Spring Boot test annotation like {@link SpringBootTest}. You
 * should configure {@link #injectHttpHostInto()} resp. {@link #injectHttpsHostInto()} in
 * order to have the mock's random base url injected into a Spring-Boot application
 * property.
 * <p>
 * By default, the mock server is only serves unencrypted HTTP. If you want to test
 * encrypted traffic using SSL, you need to specify {@link #httpsPort()} with a value
 * &gt;= 0.
 * <p>
 * The configured {@link WireMockServer} instance is made available in the application
 * context and thus can easily be injected into a test class like this:
 *
 * <pre>
 * &#64;Autowired
 * private WireMockServer wiremock;
 * </pre>
 *
 * Subsequently you can use it to configure your stubs if you refrain from using
 * {@link HttpStub annotation based} stubbing. Note that you should not call any lifecycle
 * methods like {@link WireMockServer#stop()} on the injected instance. The lifecycle will
 * be managed by this framework internally.
 *
 * @author Simon Taddiken
 * @see TestKeystores
 * @see HttpStub
 * @implNote The meta annotation {@link PropertyMapping} serves to actually make the
 *           configured values of the annotation instance accessible from the
 *           {@link WireMockInitializer}. The {@link WiremockAnnotationProps} class can be
 *           used to read the configured values from the {@link ApplicationContext}.
 */
@Retention(RUNTIME)
@Target(TYPE)
@ContextConfiguration(initializers = WireMockInitializer.class)
@PropertyMapping(WithWiremock.PREFIX)
public @interface WithWiremock {
    static final String PREFIX = "wiremock";
    static final String PROP_INJECT_HTTP_HOST_INTO = "injectHttpHostInto";
    static final String PROP_HTTP_PORT = "httpPort";
    static final String PROP_INJECT_HTTPS_HOST_INTO = "injectHttpsHostInto";
    static final String PROP_HTTPS_PORT = "httpsPort";
    static final String PROP_NEED_CLIENT_AUTH = "needClientAuth";
    static final String PROP_KEYSTORE_PASSWORD = "keystorePassword";
    static final String PROP_KEYSTORE_LOCATION = "keystoreLocation";
    static final String PROP_KEYSTORE_TYPE = "keystoreType";
    static final String PROP_TRUSTSTORE_PASSWORD = "truststorePassword";
    static final String PROP_TRUSTSTORE_LOCATION = "truststoreLocation";
    static final String PROP_TRUSTSTORE_TYPE = "truststoreType";
    static final String PROP_SSL_ONLY = "sslOnly";

    /**
     * The name of the application property that will be added and contain the wiremock's
     * http url.
     */
    @PropertyMapping(PROP_INJECT_HTTPS_HOST_INTO)
    String injectHttpsHostInto() default "";

    /**
     * The name of the application property that will be added and contain the wiremock's
     * https url.
     */
    @PropertyMapping(PROP_INJECT_HTTP_HOST_INTO)
    String injectHttpHostInto() default "";

    /**
     * Whether client authentication (via SSL client certificate) is required. When
     * {@link #truststoreLocation()} is not configured then the mock server trusts the
     * single certificate that can be retrieved using
     * {@link TestKeystores#TEST_CLIENT_CERTIFICATE}.
     */
    @PropertyMapping(PROP_NEED_CLIENT_AUTH)
    boolean needClientAuth() default false;

    /**
     * Location of the keystore to use for server side SSL. Defaults to
     * {@link TestKeystores#TEST_SERVER_CERTIFICATE}.
     */
    @PropertyMapping(PROP_KEYSTORE_LOCATION)
    String keystoreLocation() default "classpath:/certs/server_keystore.jks";

    /**
     * Type of the {@link #keystoreLocation() keystore}.
     */
    @PropertyMapping(PROP_KEYSTORE_TYPE)
    String keystoreType() default "JKS";

    /**
     * Password of the {@link #keystoreLocation() keystore}.
     */
    @PropertyMapping(PROP_KEYSTORE_PASSWORD)
    String keystorePassword() default "password";

    /**
     * Location for the trustsore to use for client side SSL. Defaults to
     * {@link TestKeystores#TEST_CLIENT_CERTIFICATE_TRUST}.
     */
    @PropertyMapping(PROP_TRUSTSTORE_LOCATION)
    String truststoreLocation() default "classpath:/certs/server_truststore.jks";

    /**
     * Password of the {@link #truststoreLocation() truststore}.
     */
    @PropertyMapping(PROP_TRUSTSTORE_PASSWORD)
    String truststorePassword() default "password";

    /**
     * Type of the {@link #truststoreLocation() truststore}.
     */
    @PropertyMapping(PROP_TRUSTSTORE_TYPE)
    String truststoreType() default "JKS";

    /**
     * Disable HTTP and only serves HTTPS.
     */
    @PropertyMapping(PROP_SSL_ONLY)
    boolean sslOnly() default false;

    /**
     * Port for HTTP. Use 0 for random port.
     */
    @PropertyMapping(PROP_HTTP_PORT)
    int httpPort() default 0;

    /**
     * Port for HTTPS. Use 0 for random port. Use -1 for disable HTTPS.
     */
    @PropertyMapping(PROP_HTTPS_PORT)
    int httpsPort() default -1;

    /**
     * Required authentication information that will be added to every stub which itself
     * doesn't specify {@link HttpStub#authenticatedBy}. Note that, once authentication is
     * configured on this level, you can not undo it for specific stubs.
     */
    Auth withGlobalAuthentication() default @Auth;
}
