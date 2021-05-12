package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Auth {

    /**
     * Required basic auth user name. Only take into consideration if
     * {@link #basicAuthPassword()} is also configured. Mutual exclusive to
     * {@link #bearerToken()}.
     */
    String basicAuthUsername() default "";

    /**
     * Required basic auth user password. Only take into consideration if
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
