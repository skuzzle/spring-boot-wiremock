package de.skuzzle.springboot.test.wiremock.stubs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Container annotation for repeatable {@link HttpStub} annotation.
 *
 * @author Simon Taddiken
 * @see HttpStub
 */
@API(status = Status.EXPERIMENTAL)
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface HttpStubs {
    /** All the stubs that should be added. */
    HttpStub[] value() default {};
}
