package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Response {

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
     * {@code "Content-Type"} is also configured using {@link #headers()}.
     */
    String contentType() default "";

    /**
     * Headers that will be added to the response. Specify pairs like
     * {@code "Content-Type=application/json"}
     */
    String[] headers() default {};
}
