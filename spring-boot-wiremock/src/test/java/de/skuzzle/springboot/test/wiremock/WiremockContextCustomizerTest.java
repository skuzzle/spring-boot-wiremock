package de.skuzzle.springboot.test.wiremock;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class WiremockContextCustomizerTest {

    @Test
    void testEquals() throws Exception {
        EqualsVerifier.forClass(WiremockContextCustomizer.class).verify();
    }
}
