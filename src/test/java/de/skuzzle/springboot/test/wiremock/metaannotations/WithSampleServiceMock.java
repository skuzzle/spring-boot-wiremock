package de.skuzzle.springboot.test.wiremock.metaannotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.http.HttpStatus;

import de.skuzzle.springboot.test.wiremock.Auth;
import de.skuzzle.springboot.test.wiremock.HttpStub;
import de.skuzzle.springboot.test.wiremock.Request;
import de.skuzzle.springboot.test.wiremock.Response;
import de.skuzzle.springboot.test.wiremock.WithWiremock;
import de.skuzzle.springboot.test.wiremock.WrapAround;

@Retention(RUNTIME)
@Target(TYPE)
@WithWiremock(injectHttpHostInto = "sample-service.url",
        withGlobalAuthentication = @Auth(
                basicAuthUsername = "user",
                basicAuthPassword = "password"))
@HttpStub(onRequest = @Request(
        toUrl = "/info"),
        respond = @Response(
                withStatus = HttpStatus.OK,
                withStatusMessage = "Everything is Ok"))
@HttpStub(onRequest = @Request(
        toUrl = "/submit/entity",
        withMethod = "PUT"),
        respond = {
                @Response(withStatus = HttpStatus.CREATED, withStatusMessage = "Entity created"),
                @Response(withStatus = HttpStatus.OK, withStatusMessage = "Entity already exists")
        }, onLastResponse = WrapAround.REPEAT)
public @interface WithSampleServiceMock {

}
