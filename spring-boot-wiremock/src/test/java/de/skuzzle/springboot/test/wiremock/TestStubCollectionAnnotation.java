package de.skuzzle.springboot.test.wiremock;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;
import de.skuzzle.springboot.test.wiremock.stubs.Request;

@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@HttpStub(onRequest = @Request(toUrl = "/fromAnnotationCollection1"))
@HttpStub(onRequest = @Request(toUrl = "/fromAnnotationCollection2"))
public @interface TestStubCollectionAnnotation {

}
