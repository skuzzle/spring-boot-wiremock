package de.skuzzle.springboot.test.wiremock.stubs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(status = Status.EXPERIMENTAL)
@Retention(RUNTIME)
public @interface Scenario {
    String name() default "";

    String state() default com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

    String nextState() default "";
}
