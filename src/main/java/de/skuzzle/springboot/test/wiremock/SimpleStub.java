package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleStub {
    String method() default "GET";

    String url() default "";

    String urlPattern() default "";

    String urlPath() default "";

    String urlPathPattern() default "";

    int status() default 200;

    String body() default "";

    String bodyBase64() default "";

    String bodyFile() default "";

    String responseContentType() default "";
}
