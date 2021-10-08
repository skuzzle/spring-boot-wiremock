package de.skuzzle.springboot.test.wiremock;

import org.junit.jupiter.api.Test;

public class TestKeystoresTest {

    @Test
    void testServerCertificateKeyStoreExists() throws Exception {
        TestKeystores.TEST_SERVER_CERTIFICATE.getKeystore();
    }

    @Test
    void testServerCertificateTrustStoreExists() throws Exception {
        TestKeystores.TEST_SERVER_CERTIFICATE_TRUST.getKeystore();
    }

    @Test
    void testClientCertificateKeyStoreExists() throws Exception {
        TestKeystores.TEST_CLIENT_CERTIFICATE.getKeystore();
    }

    @Test
    void testClientCertificateTrustStoreExists() throws Exception {
        TestKeystores.TEST_CLIENT_CERTIFICATE_TRUST.getKeystore();
    }
}
