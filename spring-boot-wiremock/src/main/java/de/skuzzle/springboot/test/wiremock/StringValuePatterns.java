package de.skuzzle.springboot.test.wiremock;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

final class StringValuePatterns {

    private StringValuePatterns() {
        // hidden
    }

    private static final Map<String, Function<String, StringValuePattern>> builders;
    static {
        final Map<String, Function<String, StringValuePattern>> temp = new LinkedHashMap<>();
        temp.put("eq:", WireMock::equalTo);
        temp.put("eqIgnoreCase:", WireMock::equalToIgnoreCase);
        temp.put("eqToJson:", WireMock::equalToJson);
        temp.put("eqToXml:", WireMock::equalToXml);
        temp.put("matching:", WireMock::matching);
        temp.put("matchingXPath:", WireMock::matchingXPath);
        temp.put("matchingJsonPath:", WireMock::matchingJsonPath);
        temp.put("notMatching:", WireMock::notMatching);
        temp.put("containing:", WireMock::containing);
        builders = Map.copyOf(temp);
    }

    public static StringValuePattern parseFromPrefix(String pattern) {
        return builders.entrySet().stream()
                .filter(entry -> pattern.startsWith(entry.getKey()))
                .map(entry -> entry.getValue().apply(pattern.substring(entry.getKey().length())))
                .findFirst()
                .orElseGet(() -> WireMock.equalTo(pattern));
    }

}
