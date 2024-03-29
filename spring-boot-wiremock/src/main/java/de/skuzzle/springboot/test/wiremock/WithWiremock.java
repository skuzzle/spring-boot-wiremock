package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.skuzzle.springboot.test.wiremock.stubs.Auth;
import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;
import de.skuzzle.springboot.test.wiremock.stubs.Request;

/**
 * Configures a WireMock server that is integrated with the Spring ApplicationContext. Use
 * in conjunction with any Spring Boot test annotation like {@link SpringBootTest}. You
 * should configure {@link #injectHttpHostInto()} resp. {@link #injectHttpsHostInto()} in
 * order to have the mock's random base url injected into a Spring-Boot application
 * property.
 * <p>
 * By default, the mock server only serves unencrypted HTTP. If you want to test encrypted
 * traffic using SSL, you need to either specify
 * {@link #randomHttpsPort()}<code>=true</code> or {@link #fixedHttpsPort()} with a value
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
 */
@API(status = Status.EXPERIMENTAL)
@Retention(RUNTIME)
@Target(TYPE)
public @interface WithWiremock {

    static final int DEFAULT_HTTP_PORT = 0;
    static final int DEFAULT_HTTPS_PORT = -1;

    /**
     * The names of the application properties that will be added and contain the
     * wiremock's http url.
     */
    String[] injectHttpsHostInto() default "";

    /**
     * The names of the application properties that will be added and contain the
     * wiremock's https url.
     */
    String[] injectHttpHostInto() default "";

    /**
     * Whether client authentication (via SSL client certificate) is required. When
     * {@link #truststoreLocation()} is not configured then the mock server trusts the
     * single certificate that can be retrieved using
     * {@link TestKeystores#TEST_CLIENT_CERTIFICATE}.
     */
    boolean needClientAuth() default false;

    /**
     * Location of the keystore to use for server side SSL. Defaults to
     * {@link TestKeystores#TEST_SERVER_CERTIFICATE}. The location will be resolved using
     * {@link ClassLoader#getResource(String)}.
     */
    String keystoreLocation() default "certs/server_keystore.jks";

    /**
     * Type of the {@link #keystoreLocation() keystore}.
     */
    String keystoreType() default "JKS";

    /**
     * Password of the {@link #keystoreLocation() keystore}.
     */
    String keystorePassword() default "password";

    /**
     * Location for the trustsore to use for client side SSL. Defaults to
     * {@link TestKeystores#TEST_CLIENT_CERTIFICATE_TRUST}. The location will be resolved
     * using {@link ClassLoader#getResource(String)}.
     */
    String truststoreLocation() default "certs/server_truststore.jks";

    /**
     * Password of the {@link #truststoreLocation() truststore}.
     */
    String truststorePassword() default "password";

    /**
     * Type of the {@link #truststoreLocation() truststore}.
     */
    String truststoreType() default "JKS";

    /**
     * Disable HTTP and only serves HTTPS.
     */
    boolean sslOnly() default false;

    /**
     * Whether to use random HTTP port. Defaults to <code>true</code> but will be silently
     * ignored if {@link #fixedHttpPort()} is specified with a value &gt; 0
     *
     * @since 0.0.15
     */
    boolean randomHttpPort() default true;

    /**
     * Enables HTTPS on a random port. Defaults to <code>false</code>. Mutual exclusive to
     * {@link #fixedHttpsPort()}.
     *
     * @since 0.0.15
     */
    boolean randomHttpsPort() default false;

    /**
     * Enables HTTP on a fixed port. If specified with a value &gt; 0 the fixed port will
     * take precedence even if {@link #randomHttpPort()} is set to <code>true</code>.
     *
     * @since 0.0.15
     */
    int fixedHttpPort() default DEFAULT_HTTP_PORT;

    /**
     * Enables HTTPS on a fixed port. Mutual exclusive to {@link #randomHttpsPort()}.
     *
     * @since 0.0.15
     */
    int fixedHttpsPort() default DEFAULT_HTTPS_PORT;

    /**
     * Required authentication information that will be added to every stub which itself
     * doesn't specify {@link Request#authenticatedBy()}. Note that, once authentication
     * is configured on this level, you can not undo it for specific stubs.
     */
    Auth withGlobalAuthentication() default @Auth;
}
