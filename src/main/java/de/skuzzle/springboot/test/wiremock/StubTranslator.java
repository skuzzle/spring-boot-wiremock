package de.skuzzle.springboot.test.wiremock;

import java.util.function.BiConsumer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

/**
 * Translates the {@link HttpStub} instance into a WireMock stub.
 *
 * @author Simon Taddiken
 */
class StubTranslator {

    static void configureStubOn(WireMockServer wiremock, HttpStub annotation) {
        wiremock.stubFor(annotation(annotation));
    }

    private static MappingBuilder annotation(HttpStub stub) {
        final ResponseDefinitionBuilder responseBuilder = WireMock.aResponse()
                .withStatus(stub.respond().withStatus().value());

        final String body = nullIfEmpty(stub.respond().withBody());
        final String bodyBase64 = nullIfEmpty(stub.respond().withBodyBase64());
        final String bodyFile = nullIfEmpty(stub.respond().withBodyFile());

        if (body != null) {
            responseBuilder.withBody(body);
        } else if (bodyBase64 != null) {
            responseBuilder.withBase64Body(bodyBase64);
        } else if (bodyFile != null) {
            responseBuilder.withBodyFile(bodyFile);
        }

        final String[] responseHeaders = stub.respond().withHeaders();
        for (final String headerAndValue : responseHeaders) {
            final String[] parts = headerAndValue.split("=", 2);
            responseBuilder.withHeader(parts[0], parts[1]);
        }

        final String responseContentType = nullIfEmpty(stub.respond().withContentType());
        if (responseContentType != null) {
            responseBuilder.withHeader("Content-Type", responseContentType);
        }

        final UrlPattern urlPattern = UrlPattern.fromOneOf(
                nullIfEmpty(stub.onRequest().toUrl()),
                nullIfEmpty(stub.onRequest().toUrlPattern()),
                nullIfEmpty(stub.onRequest().toUrlPath()),
                nullIfEmpty(stub.onRequest().toUrlPathPattern()));

        final MappingBuilder requestBuilder = WireMock.request(stub.onRequest().withMethod(), urlPattern);
        parseValueArray(stub.onRequest().containingHeaders(), requestBuilder::withHeader);
        parseValueArray(stub.onRequest().withQueryParameters(), requestBuilder::withQueryParam);
        parseValueArray(stub.onRequest().containingCookies(), requestBuilder::withCookie);

        final String basicAuthUsername = nullIfEmpty(stub.onRequest().authenticatedBy().basicAuthUsername());
        final String basicAuthPassword = nullIfEmpty(stub.onRequest().authenticatedBy().basicAuthPassword());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            requestBuilder.withBasicAuth(basicAuthUsername, basicAuthPassword);
        }
        final String bearerToken = nullIfEmpty(stub.onRequest().authenticatedBy().bearerToken());
        if (bearerToken != null) {
            requestBuilder.withHeader("Authorization", WireMock.equalToIgnoreCase("Bearer " + bearerToken));
        }

        final String requestBody = nullIfEmpty(stub.onRequest().withBody());
        if (requestBody != null) {
            requestBuilder.withRequestBody(StringValuePatterns.parseFromPrefix(requestBody));
        }
        return requestBuilder.willReturn(responseBuilder);
    }

    private static void parseValueArray(String[] array, BiConsumer<String, StringValuePattern> builder) {
        for (final String keyAndValue : array) {
            final String[] parts = keyAndValue.split("=", 2);
            final String key = parts[0];
            final String valueWithPrefix = parts[1];
            final StringValuePattern valuePattern = StringValuePatterns.parseFromPrefix(valueWithPrefix);
            builder.accept(key, valuePattern);
        }
    }

    private static String nullIfEmpty(String s) {
        return s == null || s.isEmpty() ? null : s;
    }
}
