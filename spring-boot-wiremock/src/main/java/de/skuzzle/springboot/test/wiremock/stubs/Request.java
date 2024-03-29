package de.skuzzle.springboot.test.wiremock.stubs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * Defines the stub request that will be matched in order to produce a mock response. If
 * no attributes are specified, every request will be matched.
 * <p>
 * Some attributes of this annotation support advanced matching instead of plain text
 * comparison. For example, {@code withBody = "containing:someString"} matches a body that
 * contains {@code "someString"}. There are multiple such operators that are supported:
 *
 * <table>
 * <caption>Supported string matching operations</caption>
 * <tr>
 * <th>prefix</th>
 * <th>operation</th>
 * </tr>
 * <tr>
 * <td><code>eq:</code></td>
 * <td>{@link WireMock#equalTo(String)}</td>
 * </tr>
 * <tr>
 * <td><code>eqIgnoreCase:</code></td>
 * <td>{@link WireMock#equalToIgnoreCase(String)}</td>
 * </tr>
 * <tr>
 * <td><code>eqToJson:</code></td>
 * <td>{@link WireMock#equalToJson(String)}</td>
 * </tr>
 * <tr>
 * <td><code>eqToXml:</code></td>
 * <td>{@link WireMock#equalToXml(String)}</td>
 * </tr>
 * <tr>
 * <td><code>matching:</code></td>
 * <td>{@link WireMock#matching(String)}</td>
 * </tr>
 * <tr>
 * <td><code>notMatching:</code></td>
 * <td>{@link WireMock#notMatching(String)}</td>
 * </tr>
 * <tr>
 * <td><code>matchingXPath:</code></td>
 * <td>{@link WireMock#matchingXPath(String)}</td>
 * </tr>
 * <tr>
 * <td><code>matchingJsonPath:</code></td>
 * <td>{@link WireMock#matchingJsonPath(String)}</td>
 * </tr>
 * <tr>
 * <td><code>containing:</code></td>
 * <td>{@link WireMock#containing(String)}</td>
 * </tr>
 * </table>
 * With no prefix the string comparison defaults to <em>eq:</em>.
 *
 * @author Simon Taddiken
 * @see Response
 */
@API(status = Status.EXPERIMENTAL)
@Retention(RUNTIME)
public @interface Request {

    // sentinel value to signal that no priority was configured
    static int NO_PRIORITY = Integer.MAX_VALUE - 1;

    /** The stub's priority. */
    int priority() default NO_PRIORITY;

    /**
     * Allows to configure WireMock scenarios that can be used for stateful request
     * matching.
     * <p>
     * Note that this attribute must be left empty when used within a {@link HttpStub}
     * with multiple responses configured.
     */
    Scenario scenario() default @Scenario;

    /**
     * Authentication information required for the stub to match. By default, no
     * authentication is required.
     */
    Auth authenticatedBy() default @Auth;

    /** Request method for this stub. If not specified, every method will be matched. */
    String withMethod() default "ANY";

    /**
     * The URL for this stub. Mutual exclusive to {@link #toUrlPattern()},
     * {@link #toUrlPath()} and {@link #toUrlPathPattern()}. If not specified, every url
     * will be matched.
     * <p>
     * Warning: Using {@link #toUrl()} in combination with {@link #withQueryParameters()}
     * will effectively result in a conflicting stub definition that will never match. Use
     * {@link #toUrlPath()} instead.
     */
    String toUrl() default "";

    String toUrlPattern() default "";

    /**
     * The URL path for this stub. Mutual exclusive to {@link #toUrlPattern()},
     * {@link #toUrl()} and {@link #toUrlPathPattern()}. If not specified, every url will
     * be matched.
     */
    String toUrlPath() default "";

    String toUrlPathPattern() default "";

    /**
     * The expected body to match. You can optionally prefix the string with a matching
     * operator like {@code containing:} or {@code matching:} By default, matches every
     * body.
     */
    String withBody() default "";

    /**
     * Headers to match. You can specify key/value pairs and optionally operators for
     * value matching like so:
     *
     * <pre>
     * containingHeaders = {
     *     "If-None-Match=matching:[a-z0-9-]",
     *     "Content-Type=application/json"
     * }
     * </pre>
     *
     * See the documentation for {@link Request} for a list of supported operators.
     */
    String[] containingHeaders() default {};

    /**
     * Cookies to match. You can specify key/value pairs and optionally operators for
     * value matching like so:
     *
     * <pre>
     * containingCookies = {
     *     "jsessionId=matching:[a-z0-9]"
     * }
     * </pre>
     *
     * See the documentation for {@link Request} for a list of supported operators.
     */
    String[] containingCookies() default {};

    /**
     * Query parameters to match. You can specify key/value pairs and optionally operators
     * for value matching like so:
     *
     * <pre>
     * withQueryParameters = {
     *     "search=eqIgnoreCase:searchterm",
     *     "limit=100"
     * }
     * </pre>
     *
     * See the documentation for {@link Request} for a list of supported operators.
     * <p>
     * Note: Doesn't work in combination with {@link #toUrl()} but you can use
     * {@link #toUrlPath()} instead. See related GitHub issue:
     * https://github.com/tomakehurst/wiremock/issues/1262
     */
    String[] withQueryParameters() default {};

}
