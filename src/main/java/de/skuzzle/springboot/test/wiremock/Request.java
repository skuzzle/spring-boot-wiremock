package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Request {

    /** Request method for this stub. Defaults to GET. */
    String withMethod() default "GET";

    /**
     * The URL for this stub. Mutual exclusive to {@link #toUrlPattern()},
     * {@link #toUrlPath()} and {@link #toUrlPathPattern()}. If not specified, every url will
     * be matched.
     */
    String toUrl() default "";

    String toUrlPattern() default "";

    String toUrlPath() default "";

    String toUrlPathPattern() default "";

    String[] containingHeaders() default {};
}
