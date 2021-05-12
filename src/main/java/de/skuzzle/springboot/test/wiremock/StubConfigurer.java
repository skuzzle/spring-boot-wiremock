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
                nullIfEmpty(stub.url()),
                nullIfEmpty(stub.urlPattern()),
                nullIfEmpty(stub.urlPath()),
                nullIfEmpty(stub.urlPathPattern()));

        final ResponseDefinitionBuilder responseBuilder = WireMock.aResponse()
                .withStatus(stub.status());

        final String body = nullIfEmpty(stub.body());
        final String bodyBase64 = nullIfEmpty(stub.bodyBase64());
        final String bodyFile = nullIfEmpty(stub.bodyFile());

        if (body != null) {
            responseBuilder.withBody(body);
        } else if (bodyBase64 != null) {
            responseBuilder.withBase64Body(bodyBase64);
        } else if (bodyFile != null) {
            responseBuilder.withBodyFile(bodyFile);
        }

        final String[] responseHeaders = stub.responseHeaders();
        for (final String headerAndValue : responseHeaders) {
            final String[] parts = headerAndValue.split("=", 2);
            responseBuilder.withHeader(parts[0], parts[1]);
        }

        final String responseContentType = nullIfEmpty(stub.responseContentType());
        if (responseContentType != null) {
            responseBuilder.withHeader("Content-Type", responseContentType);
        }

        final MappingBuilder requestBuilder = WireMock.request(stub.method(), urlPattern);
        final String basicAuthUsername = nullIfEmpty(stub.basicAuthUsername());
        final String basicAuthPassword = nullIfEmpty(stub.basicAuthPassword());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            requestBuilder.withBasicAuth(basicAuthUsername, basicAuthPassword);
        }
        final String bearerToken = nullIfEmpty(stub.bearerToken());
        if (bearerToken != null) {
            requestBuilder.withHeader("Authorization", WireMock.equalToIgnoreCase("Bearer " + bearerToken));
        }
        return requestBuilder.willReturn(responseBuilder);
    }

    private static String nullIfEmpty(String s) {
        return s == null || s.isEmpty() ? null : s;
    }
}
