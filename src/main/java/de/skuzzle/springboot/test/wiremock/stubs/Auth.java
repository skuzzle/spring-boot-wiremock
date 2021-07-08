package de.skuzzle.springboot.test.wiremock.stubs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * For defining authentication information for a stub. If no attributes are specified, no
 * authentication will be required in order for the stub to match.
 *
 * @author Simon Taddiken
 * @see Request
 */
@Retention(RUNTIME)
public @interface Auth {

    /**
     * Required basic auth user name. Only taken into consideration if
     * {@link #basicAuthPassword()} is also configured. Mutual exclusive to
     * {@link #bearerToken()}.
     */
    String basicAuthUsername() default "";

    /**
     * Required basic auth user password. Only taken into consideration if
     * {@link #basicAuthUsername()} is also configured. Mutual exclusive to
     * {@link #bearerToken()}.
     */
    String basicAuthPassword() default "";

    /**
     * Required bearer token (case insensitive). Mutual exclusive to
     * {@link #basicAuthUsername()} and {@link #basicAuthPassword()}.
     */
    String bearerToken() default "";
}
