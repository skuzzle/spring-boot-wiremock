package de.skuzzle.springboot.test.wiremock;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.google.common.base.Preconditions;

/**
 * Translates the {@link HttpStub} instance into a WireMock stub.
 *
 * @author Simon Taddiken
 */
class StubTranslator {

    static void configureStubOn(WireMockServer wiremock, HttpStub stub) {
        final boolean multipleResponseStubs = stub.respond().length > 1;
        Preconditions.checkArgument(!multipleResponseStubs ||
                nullIfEmpty(stub.onRequest().scenario().name()) == null,
                "Scenario not supported within stub with multiple responses");

        final Iterator<Response> responses = Arrays.asList(stub.respond()).iterator();

        int state = 0;
        while (responses.hasNext()) {
            final Response response = responses.next();

            final MappingBuilder requestBuilder = buildRequest(stub.onRequest());

            if (multipleResponseStubs) {
                final String scenarioName = stub.toString();

                final int nextState = stub.wrapAround() && !responses.hasNext()
                        ? 0
                        : state + 1;

                requestBuilder.inScenario(scenarioName)
                        .whenScenarioStateIs(translateState(state))
                        .willSetStateTo(translateState(nextState));
            }

            final ResponseDefinitionBuilder responseBuilder = buildResponse(response);
            wiremock.stubFor(requestBuilder.willReturn(responseBuilder));
            ++state;
        }
    }

    private static String translateState(int state) {
        return state == 0
                ? com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
                : "" + state;
    }

    private static MappingBuilder buildRequest(Request request) {
        final String toUrl = nullIfEmpty(request.toUrl());
        final String toUrlPattern = nullIfEmpty(request.toUrlPattern());
        final String toUrlPath = nullIfEmpty(request.toUrlPath());
        final String toUrlPathPattern = nullIfEmpty(request.toUrlPathPattern());
        mutuallyExclusive(
                parameters("url", "urlPattern", "urlPath", "urlPathPattern"),
                values(toUrl, toUrlPattern, toUrlPath, toUrlPathPattern));
        final UrlPattern urlPattern = UrlPattern.fromOneOf(toUrl, toUrlPattern, toUrlPath, toUrlPathPattern);

        final MappingBuilder requestBuilder = WireMock.request(request.withMethod(), urlPattern);

        final Scenario scenario = request.scenario();
        final String scenarioName = nullIfEmpty(scenario.name());
        if (scenarioName != null) {
            final String scenarioState = nullIfEmpty(scenario.state());
            final String nextState = defaultIfEmpty(scenario.nextState(), scenarioName);
            requestBuilder.inScenario(scenarioName)
                    .whenScenarioStateIs(scenarioState)
                    .willSetStateTo(nextState);
        }

        parseValueArray(request.containingHeaders(), requestBuilder::withHeader);
        parseValueArray(request.withQueryParameters(), requestBuilder::withQueryParam);
        parseValueArray(request.containingCookies(), requestBuilder::withCookie);

        final String basicAuthUsername = nullIfEmpty(request.authenticatedBy().basicAuthUsername());
        final String basicAuthPassword = nullIfEmpty(request.authenticatedBy().basicAuthPassword());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            requestBuilder.withBasicAuth(basicAuthUsername, basicAuthPassword);
        }
        final String bearerToken = nullIfEmpty(request.authenticatedBy().bearerToken());
        if (bearerToken != null) {
            requestBuilder.withHeader("Authorization", WireMock.equalToIgnoreCase("Bearer " + bearerToken));
        }
        mutuallyExclusive(parameters("basicAuthPassword", "bearerToken"), values(basicAuthPassword, bearerToken));
        mutuallyExclusive(parameters("basicAuthUsername", "bearerToken"), values(basicAuthUsername, bearerToken));

        final String requestBody = nullIfEmpty(request.withBody());
        if (requestBody != null) {
            requestBuilder.withRequestBody(StringValuePatterns.parseFromPrefix(requestBody));
        }

        final int priority = request.priority();
        if (priority != Request.NO_PRIORITY) {
            requestBuilder.atPriority(priority);
        }

        return requestBuilder;
    }

    private static ResponseDefinitionBuilder buildResponse(Response response) {
        final ResponseDefinitionBuilder responseBuilder = WireMock.aResponse()
                .withStatus(response.withStatus().value());

        final String statusMessage = nullIfEmpty(response.withStatusMessage());
        if (statusMessage != null) {
            responseBuilder.withStatusMessage(statusMessage);
        }

        final String body = nullIfEmpty(response.withBody());
        final String bodyBase64 = nullIfEmpty(response.withBodyBase64());
        final String bodyFile = nullIfEmpty(response.withBodyFile());
        mutuallyExclusive(
                parameters("body", "bodyBase64", "bodyFile"),
                values(body, bodyBase64, bodyFile));

        if (body != null) {
            responseBuilder.withBody(body);
        } else if (bodyBase64 != null) {
            responseBuilder.withBase64Body(bodyBase64);
        } else if (bodyFile != null) {
            responseBuilder.withBodyFile(bodyFile);
        }

        final String[] responseHeaders = response.withHeaders();
        for (final String headerAndValue : responseHeaders) {
            final String[] parts = headerAndValue.split("=", 2);
            responseBuilder.withHeader(parts[0], parts[1]);
        }

        final String responseContentType = nullIfEmpty(response.withContentType());
        if (responseContentType != null) {
            responseBuilder.withHeader("Content-Type", responseContentType);
        }
        return responseBuilder;
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

    private static String defaultIfEmpty(String s, String defaultValue) {
        return s == null || s.isEmpty() ? defaultValue : s;
    }

    private static String nullIfEmpty(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    private static void mutuallyExclusive(String[] names, Object[] args) {
        final long notNullCount = Arrays.stream(args).filter(arg -> arg != null).count();
        Preconditions.checkArgument(notNullCount <= 1,
                "Parameters '%s' are mutually exclusive and only one must be specified", Arrays.toString(names));
    }

    private static String[] parameters(String... names) {
        return names;
    }

    private static Object[] values(Object... values) {
        return values;
    }

}
