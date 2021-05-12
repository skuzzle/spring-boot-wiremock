package de.skuzzle.springboot.test.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

class StubConfigurer {

    static void configureStubOn(WireMockServer wiremock, SimpleStub annotation) {
        wiremock.stubFor(annotation(annotation));
    }

    private static MappingBuilder annotation(SimpleStub stub) {
        final UrlPattern urlPattern = UrlPattern.fromOneOf(
                nullIfEmpty(stub.request().url()),
                nullIfEmpty(stub.request().urlPattern()),
                nullIfEmpty(stub.request().urlPath()),
                nullIfEmpty(stub.request().urlPathPattern()));

        final ResponseDefinitionBuilder responseBuilder = WireMock.aResponse()
                .withStatus(stub.response().status());

        final String body = nullIfEmpty(stub.response().body());
        final String bodyBase64 = nullIfEmpty(stub.response().bodyBase64());
        final String bodyFile = nullIfEmpty(stub.response().bodyFile());

        if (body != null) {
            responseBuilder.withBody(body);
        } else if (bodyBase64 != null) {
            responseBuilder.withBase64Body(bodyBase64);
        } else if (bodyFile != null) {
            responseBuilder.withBodyFile(bodyFile);
        }

        final String[] responseHeaders = stub.response().headers();
        for (final String headerAndValue : responseHeaders) {
            final String[] parts = headerAndValue.split("=", 2);
            responseBuilder.withHeader(parts[0], parts[1]);
        }

        final String responseContentType = nullIfEmpty(stub.response().contentType());
        if (responseContentType != null) {
            responseBuilder.withHeader("Content-Type", responseContentType);
        }

        final MappingBuilder requestBuilder = WireMock.request(stub.request().method(), urlPattern);
        final String[] requestHeaders = stub.request().headers();
        for (final String headerAndValue : requestHeaders) {
            final String[] parts = headerAndValue.split("=", 2);
            requestBuilder.withHeader(parts[0], WireMock.equalTo(parts[1]));
        }

        final String basicAuthUsername = nullIfEmpty(stub.auth().basicAuthUsername());
        final String basicAuthPassword = nullIfEmpty(stub.auth().basicAuthPassword());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            requestBuilder.withBasicAuth(basicAuthUsername, basicAuthPassword);
        }
        final String bearerToken = nullIfEmpty(stub.auth().bearerToken());
        if (bearerToken != null) {
            requestBuilder.withHeader("Authorization", WireMock.equalToIgnoreCase("Bearer " + bearerToken));
        }
        return requestBuilder.willReturn(responseBuilder);
    }

    private static String nullIfEmpty(String s) {
        return s == null || s.isEmpty() ? null : s;
    }
}
