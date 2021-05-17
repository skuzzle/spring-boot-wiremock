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
@Repeatable(HttpStubs.class)
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpStub {

    /**
     * The request that must be matched in order to produce a mock {@link #respond()
     * response}. By default, every request will be matched.
     */
    Request onRequest() default @Request;

    /**
     * The mock response that will be returned by the server if an incoming request
     * matched what has been configured in {@link #onRequest()}. By default, returns an
     * empty 200 OK message with no further details.
     */
    Response respond() default @Response;

}
