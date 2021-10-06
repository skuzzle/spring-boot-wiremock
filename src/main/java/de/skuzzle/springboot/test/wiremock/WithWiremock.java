package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

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
 */
@API(status = Status.EXPERIMENTAL)
@Retention(RUNTIME)
@Target(TYPE)
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = WithWiremockTestExecutionListener.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public @interface WithWiremock {

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
     * {@link TestKeystores#TEST_SERVER_CERTIFICATE}.
     */
    String keystoreLocation() default "classpath:/certs/server_keystore.jks";

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
     * {@link TestKeystores#TEST_CLIENT_CERTIFICATE_TRUST}.
     */
    String truststoreLocation() default "classpath:/certs/server_truststore.jks";

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
     * Port for HTTP. Use 0 for random port.
     */
    int httpPort() default 0;

    /**
     * Port for HTTPS. Use 0 for random port. Use -1 to disable HTTPS.
     */
    int httpsPort() default -1;

    /**
     * Required authentication information that will be added to every stub which itself
     * doesn't specify {@link Request#authenticatedBy()}. Note that, once authentication
     * is configured on this level, you can not undo it for specific stubs.
     */
    Auth withGlobalAuthentication() default @Auth;
}
