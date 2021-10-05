package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

public class NestedTestTest {

    @SpringBootTest
    @WithWiremock(injectHttpHostInto = { "httpHost1", "httpHost2" })
    static class Nested1 {

        @Value("${httpHost1}")
        private String host1;
        @Value("${httpHost2}")
        private String host2;

        @Test
        void testInjectHost1() throws Exception {
            assertThat(host1).startsWith("http:");
        }

        @Test
        void testInjectHost2() throws Exception {
            assertThat(host2).startsWith("http:");
        }
    }

    @SpringBootTest
    @WithWiremock(injectHttpsHostInto = { "httpsHost1", "httpsHost2" }, httpsPort = 0)
    static class Nested2 {

        @Value("${httpsHost1}")
        private String host1;
        @Value("${httpsHost2}")
        private String host2;

        @Test
        void testInjectHost1() throws Exception {
            assertThat(host1).startsWith("https:");
        }

        @Test
        void testInjectHost2() throws Exception {
            assertThat(host2).startsWith("https:");
        }
    }
}
