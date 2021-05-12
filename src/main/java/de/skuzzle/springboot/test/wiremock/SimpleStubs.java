package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Container annotation for repeatable {@link SimpleStub} annotation.
 *
 * @author Simon Taddiken
 */
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleStubs {
    /** All the stubs that should be added. */
    SimpleStub[] value() default {};
}
