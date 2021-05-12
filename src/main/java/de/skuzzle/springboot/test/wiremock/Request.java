package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Request {

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

    String toUrlPath() default "";

    String toUrlPathPattern() default "";

    /**
     * The expected body to match. You can optionally prefix the string with a matching
     * operator like {@code containing:} or {@code matching:} By default, matches every
     * body.
     */
    String withBody() default "";

    String[] containingHeaders() default {};

    String[] containingCookies() default {};

    /**
     * Note: Doesn't work in combination with {@link #toUrl()} but you can use
     * {@link #toUrlPath()} instead. See related GitHub issue:
     * https://github.com/tomakehurst/wiremock/issues/1262
     */
    String[] withQueryParameters() default {};

    Auth authenticatedBy() default @Auth;
}
