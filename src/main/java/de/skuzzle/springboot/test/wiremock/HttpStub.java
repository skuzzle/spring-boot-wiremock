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
 * It is possible to configure multiple {@link #respond() responses}. If more than one
 * response is specified, the responses will be returned consecutively for each matched
 * request. When {@link #wrapAround()} is <code>true</code>, the stub will start over with
 * the first response, once the last has been returned.
 * <p>
 * The annotation can be put in versatile places:
 * <ul>
 * <li>You can place the annotation on a single test method.</li>
 * <li>You can place the annotation on the test class itself to define global stubs.</li>
 * <li>You can place the annotation on a super class or any implemented interface of a
 * test class for easy reuse of the stub.</li>
 * <li>You can place the annotation as meta-annotation on a custom annotation type for
 * easy reuse of the stub.</li>
 * <li>The annotation is repeatable. Wherever you put a single instance, you can also put
 * multiple instances to define multiple stubs.</li>
 * </ul>
 * Note that all stubs are reset after each test.
 * <p>
 * If you need more sophisticated stubbing, you can always just autowire the
 * {@link WireMockServer} into your test class and use
 * {@link WireMockServer#stubFor(com.github.tomakehurst.wiremock.client.MappingBuilder)}.
 * The same approach should be used to use verifications which are not supported via
 * annotations.
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
     * The mock responses that will consecutively be returned by the server if an incoming
     * request matched what has been configured in {@link #onRequest()}. By default,
     * returns an empty 200 OK message with no further details.
     */
    Response[] respond() default { @Response };

    /**
     * Whether to start over with the first response once the last response has been
     * returned. Only applies if more then one {@link #respond() response} has been
     * configured. Defaults to <code>false</code>.
     *
     * @deprecated Since 0.0.12. Use {@link #onLastResponse()} with
     *             {@link WrapAround#START_OVER} instead.
     */
    @Deprecated(forRemoval = true, since = "0.0.12")
    boolean wrapAround() default false;

    /**
     * Defines the response behavior of the mock if multiple responses are defined. By
     * default, when the last response has been returned, the mock will answer with a 403
     * status code (see {@link WrapAround#RETURN_ERROR}).
     */
    WrapAround onLastResponse() default WrapAround.RETURN_ERROR;
}
