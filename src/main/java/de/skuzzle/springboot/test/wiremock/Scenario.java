package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Scenario {
    String name() default "";

    String state() default com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

    String nextState() default "";
}
