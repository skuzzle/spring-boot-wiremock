package de.skuzzle.springboot.test.wiremock.stubs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.http.HttpStatus;

/**
 * Defines the contents of the mock response that will be sent when the request of a
 * {@link HttpStub stub} was matched. When no attributes are defined, the response will be
 * empty with status 200 OK.
 *
 * @author Simon Taddiken
 */
@API(status = Status.EXPERIMENTAL)
@Retention(RUNTIME)
public @interface Response {

    /** The HTTP status of the response. Defaults to {@link HttpStatus#OK}. */
    HttpStatus withStatus() default HttpStatus.OK;

    /** The HTTP status line. */
    String withStatusMessage() default "";

    /**
     * The body of the response as json string. Will also set the Content-Type to
     * <code>application/json</code>. The Content-Type can still be overridden by
     * {@link #withContentType()} or {@link #withHeaders()}.
     * <p>
     * Mutual exclusive to {@link #withBody()}, {@link #withBodyFile()} and
     * {@link #withBodyBase64()}. Defaults to 'no body'.
     *
     * @since 0.0.17
     */
    String withJsonBody() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #withBodyBase64()},
     * {@link #withJsonBody()} and {@link #withBodyFile()}. Defaults to 'no body'.
     */
    String withBody() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #withBody()},
     * {@link #withJsonBody()} and {@link #withBodyFile()}. Defaults to 'no body'.
     */
    String withBodyBase64() default "";

    /**
     * The body of the response. Mutual exclusive to {@link #withBody()},
     * {@link #withJsonBody()} and {@link #withBodyBase64()}. Defaults to 'no body'.
     * <p>
     * By default, files must be contained in a folder on the classpath called
     * {@code __files}.
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
