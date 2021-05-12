package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.tomakehurst.wiremock.WireMockServer;

/**
 * Allows to configure a simple, single stub for a test case by annotating the test
 * method. If you need more sophisticated stubbing, you can always just autowire the
 * {@link WireMockServer} into your test class and use
 * {@link WireMockServer#stubFor(com.github.tomakehurst.wiremock.client.MappingBuilder)}.
 *
 * @author Simon Taddiken
 */
@Repeatable(SimpleStubs.class)
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleStub {
    /** Request method for this stub. Defaults to GET. */
    String method() default "GET";

    /**
     * The URL for this stub. Mutual exclusive to {@link #urlPattern()},
     * {@link #urlPath()} and {@link #urlPathPattern()}. If not specified, every url will
     * be matched.
     */
    String url() default "";

    String urlPattern() default "";

    String urlPath() default "";

    String urlPathPattern() default "";

    /**
     * Required basic auth user name. Only take into consideration if
     * {@link #basicAuthPassword()} is also configured. Mutual exclusive to
     * {@link #bearerToken()}.
     */
    String basicAuthUsername() default "";

    /**
     * Required basic auth user password. Only take into consideration if
     * {@link #basicAuthUsername()} is also configured. Mutual exclusive to
     * {@link #bearerToken()}.
     */
    String basicAuthPassword() default "";

    /**
     * Required bearer token (case insensitive). Mutual exclusive to
     * {@link #basicAuthUsername()} and {@link #basicAuthPassword()}.
     */
    String bearerToken() default "";

    /** The HTTP status of the response. Defaults to 200 */
    int status() default 200;

    /**
     * The body of the response. Mutual exclusive to {@link #bodyBase64()} and
     * {@link #bodyFile()}. Defaults to 'no body'.
     */
    String body() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #body()} and
     * {@link #bodyFile()}. Defaults to 'no body'.
     */
    String bodyBase64() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #body()} and
     * {@link #bodyBase64()}. Defaults to 'no body'.
     */
    String bodyFile() default "";

    /**
     * Content-Type for the response. If configured, this value takes precedence if
     * {@code "Content-Type"} is also configured using {@link #responseHeaders()}.
     */
    String responseContentType() default "";

    /**
     * Headers that will be added to the response. Specify pairs like
     * {@code "Content-Type=application/json"}
     */
    String[] responseHeaders() default {};
}
