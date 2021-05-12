package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Request {

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

    String[] headers() default {};
}
