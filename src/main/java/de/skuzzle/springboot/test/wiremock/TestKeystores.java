package de.skuzzle.springboot.test.wiremock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.google.common.io.Resources;

public final class TestKeystores {

    private TestKeystores() {
    }

    public static final KeystoreLocation TEST_CLIENT_CERTIFICATE = new KeystoreLocation(
            "/certs/client_keystore.pkcs12",
            "password",
            "PKCS12");
    public static final KeystoreLocation TEST_CLIENT_CERTIFICATE_TRUST = new KeystoreLocation(
            "/certs/server_truststore.jks",
            "password",
            "JKS");
    public static final KeystoreLocation TEST_SERVER_CERTIFICATE = new KeystoreLocation(
            "/certs/server_keystore.jks",
            "password",
            "JKS");
    public static final KeystoreLocation TEST_SERVER_CERTIFICATE_TRUST = new KeystoreLocation(
            "/certs/client_truststore.jks",
            "password",
            "JKS");

    public static final class KeystoreLocation {
        private final String classpathLocation;
        private final String password;
        private final String type;

        private KeystoreLocation(String classpathLocation, String password, String type) {
            this.classpathLocation = classpathLocation;
            this.password = password;
            this.type = type;
        }

        public URL toURL() {
            return Resources.getResource(classpathLocation);
        }

        public String getLocation() {
            return toURL().toString();
        }

        public KeyStore getKeystore() {
            try (InputStream in = toURL().openStream()) {
                final KeyStore keyStore = KeyStore.getInstance(type);
                keyStore.load(in, password.toCharArray());
                return keyStore;
            } catch (final KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
                throw new IllegalStateException(
                        "Could not read keystore from classpath location: " + classpathLocation);
            }
        }
    }
}
