package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

public class NestedTestTest {

    @SpringBootTest
    @WithWiremock(injectHttpHostInto = "httpHost")
    static class Nested1 {
        @Value("${httpHost}")
        private String host;

        @Test
        void testName() throws Exception {
            assertThat(host).startsWith("http:");
        }
    }

    @SpringBootTest
    @WithWiremock(injectHttpsHostInto = "httpsHost", httpsPort = 0)
    static class Nested2 {

        @Value("${httpsHost}")
        private String host;

        @Test
        void testName() throws Exception {
            assertThat(host).startsWith("https:");
        }
    }
}
