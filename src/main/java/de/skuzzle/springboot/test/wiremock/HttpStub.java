package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.tomakehurst.wiremock.WireMockServer;

/**
 * Allows to configure a simple, single stub for a test case by annotating the test method
 * or the test class. All attributes are optional. An empty {@code @HttpStub} on a test
 * method will set up a stub that returns a simple {@code 200 OK} response for every
 * incoming request.
 * <p>
 * In order to refine the matching and the produced response, see {@link #onRequest()} and
 * {@link #respond()}. Here is a basic example:
 *
 * <pre>
 * &#64;Test
 * &#64;HttpStub(
 *     onRequest = &#64;Request(
 *         toUrl("/createItem"),
 *         withMethod("POST"),
 *         withCookie("jsessionid=matching:[a-z0-9]+")
 *         authenticatedBy = &#64;Auth(
 *             basicAuthUsername = "username",
 *             basicAuthPassword = "password"))
 *     respond = &#64;Response(
 *         withStatus(HttpStatus.CREATED),
 *         withHeader("location="/newItem"),
 *         withBody("{ \"status\": \"SUCCESS\" }")))
 * void testCreateItem() {
 *
 * }
 * </pre>
 * <p>
 * The annotation is repeatable. You can place multiple instances on a test to define
 * multiple stubs. You can also place the annotation on the test class itself to define
 * global stubs. Note that all stubs are reset after each test.
 * <p>
 * If you need more sophisticated stubbing, you can always just autowire the
 * {@link WireMockServer} into your test class and use
 * {@link WireMockServer#stubFor(com.github.tomakehurst.wiremock.client.MappingBuilder)}.
 *
 * @author Simon Taddiken
 * @see Request
 * @see Response
 */
@Repeatable(HttpStubs.class)
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
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
