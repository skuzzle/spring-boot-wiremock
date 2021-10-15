package de.skuzzle.springboot.test.wiremock;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class WiremockAnnotationConfigurationTest {
    @Test
    void testEquals() throws Exception {
        EqualsVerifier.forClass(WiremockAnnotationConfiguration.class).verify();
    }
}
