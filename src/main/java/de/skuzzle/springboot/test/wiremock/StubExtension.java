package de.skuzzle.springboot.test.wiremock;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

class StubExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        final Method method = context.getRequiredTestMethod();
        final SimpleStub stub = method.getAnnotation(SimpleStub.class);
        if (stub == null) {
            return;
        }
        final WireMockServer wireMockServer = WireMockHolder.getServer();
        wireMockServer.stubFor(annotation(stub));
    }

    private MappingBuilder annotation(SimpleStub stub) {
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

        final String responseContentType = nullIfEmpty(stub.responseContentType());
        if (responseContentType != null) {
            responseBuilder.withHeader("Content-Type", responseContentType);
        }

        return WireMock.request(stub.method(), urlPattern)
                .willReturn(responseBuilder);
    }

    private String nullIfEmpty(String s) {
        return s == null || s.isEmpty() ? null : s;
    }
}
