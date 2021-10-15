package de.skuzzle.springboot.test.wiremock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

public class NestedTestTest {

    @SpringBootTest
    @WithWiremock(injectHttpHostInto = { "httpHost1", "httpHost2" })
    static class TestInjectMultipleHttpHosts {

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
    @WithWiremock(injectHttpsHostInto = { "httpsHost1", "httpsHost2" }, randomHttpsPort = true)
    static class TestInjectMultipleHttpsHosts {

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

    @SpringBootTest
    @WithWiremock(fixedHttpPort = 13337)
    static class TestFixedHttpPort {
        @Value("${wiremock.server.httpHost}")
        private String host;

        @Test
        void testInjectHost1() throws Exception {
            assertThat(host).endsWith(":13337");
        }
    }

    @SpringBootTest
    @WithWiremock(fixedHttpPort = 13338, randomHttpPort = true)
    static class TestFixedHttpPortTakesPrecedenceOverRandomHttpPort {
        @Value("${wiremock.server.httpHost}")
        private String host;

        @Test
        void testInjectHost() throws Exception {
            assertThat(host).endsWith(":13338");
        }
    }

    @SpringBootTest
    @WithWiremock(fixedHttpsPort = 13339)
    static class TestFixedHttpsPort {
        @Value("${wiremock.server.httpsHost}")
        private String host;

        @Test
        void testInjectHost() throws Exception {
            assertThat(host).endsWith(":13339");
        }
    }

    @SpringBootTest
    @WithWiremock(randomHttpsPort = true)
    static class TestRandomHttpsHost {
        @Value("${wiremock.server.httpsHost}")
        private String host;

        @Test
        void testContextStarts() throws Exception {
        }
    }

    @SpringBootTest
    @EnableConfigurationProperties(TestConfigurationProperties.class)
    @WithWiremock(injectHttpHostInto = "url.in.propertiesfile")
    static class InjectIntoConfigurationProperties {
        @Autowired
        private TestConfigurationProperties properties;
        @Value("${url.in.propertiesfile}")
        private String host;

        @Test
        void testWiremockLocationTakesPrecedenceOverStaticConfiguration() throws Exception {
            assertThat(properties.getPropertiesfile()).isEqualTo(host);
        }
    }

    @ConfigurationProperties("url.in")
    static class TestConfigurationProperties {
        private String propertiesfile;

        public String getPropertiesfile() {
            return this.propertiesfile;
        }

        public void setPropertiesfile(String propertiesfile) {
            this.propertiesfile = propertiesfile;
        }
    }
}
