package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.http.HttpStatus;

@Retention(RUNTIME)
public @interface Response {

    /** The HTTP status of the response. Defaults to {@link HttpStatus#OK}. */
    HttpStatus withStatus() default HttpStatus.OK;

    String withStatusMessage() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #withBodyBase64()} and
     * {@link #withBodyFile()}. Defaults to 'no body'.
     */
    String withBody() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #withBody()} and
     * {@link #withBodyFile()}. Defaults to 'no body'.
     */
    String withBodyBase64() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #withBody()} and
     * {@link #withBodyBase64()}. Defaults to 'no body'.
     */
    String withBodyFile() default "";

    /**
     * Content-Type for the response. If configured, this value takes precedence if
     * {@code "Content-Type"} is also configured using {@link #withHeaders()}.
     */
    String withContentType() default "";

    /**
     * Headers that will be added to the response. Specify pairs like
     * {@code "Content-Type=application/json"}
     */
    String[] withHeaders() default {};
}
