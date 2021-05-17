package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Request {

    /**
     * Allows to configure WireMock scenarios that can be used for stateful request
     * matching.
     */
    Scenario scenario() default @Scenario;

    /** Request method for this stub. If not specified, every method will be matched. */
    String withMethod() default "ANY";

    /**
     * The URL for this stub. Mutual exclusive to {@link #toUrlPattern()},
     * {@link #toUrlPath()} and {@link #toUrlPathPattern()}. If not specified, every url
     * will be matched. Mutual exclusive to {@link #toUrlPath()},
     * {@link #toUrlPathPattern()} and {@link #toUrlPattern()}.
     * <p>
     * Warning: Using {@link #toUrl()} in combination with {@link #withQueryParameters()}
     * will effectively result in a conflicting stub definition that will never match. Use
     * {@link #toUrlPath()} instead.
     */
    String toUrl() default "";

    String toUrlPattern() default "";

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
     * <p>
     * Note: Doesn't work in combination with {@link #toUrl()} but you can use
     * {@link #toUrlPath()} instead. See related GitHub issue:
     * https://github.com/tomakehurst/wiremock/issues/1262
     */
    String[] withQueryParameters() default {};

    /**
     * Authentication information required for the stub to match. By default, no
     * authentication is required.
     */
    Auth authenticatedBy() default @Auth;
}
