package de.skuzzle.springboot.test.wiremock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.google.common.io.Resources;

/**
 * Holds the locations for the default key- and truststores that are used for mock SSL
 * connections. These certificates can be used if no custom key- or truststore have been
 * configured in your test.
 * <p>
 * <b>Never use any of these in production.</b>
 *
 * @author Simon Taddiken
 */
@API(status = Status.EXPERIMENTAL)
public final class TestKeystores {

    private TestKeystores() {
    }

    /**
     * A keystore containing a client certificate that is considered valid by the mock
     * server.
     */
    public static final KeystoreLocation TEST_CLIENT_CERTIFICATE = new KeystoreLocation(
            "certs/client_keystore.pkcs12",
            "password",
            "PKCS12");
    /**
     * A truststore for trusting the client certificate contained in
     * {@link #TEST_CLIENT_CERTIFICATE}.
     */
    public static final KeystoreLocation TEST_CLIENT_CERTIFICATE_TRUST = new KeystoreLocation(
            "certs/server_truststore.jks",
            "password",
            "JKS");
    /**
     * A keystore containing a self signed server certificate which is used by the mock.
     */
    public static final KeystoreLocation TEST_SERVER_CERTIFICATE = new KeystoreLocation(
            "certs/server_keystore.jks",
            "password",
            "JKS");
    /**
     * A truststore for trusting the self signed server certificate contained in
     * {@link #TEST_SERVER_CERTIFICATE}
     */
    public static final KeystoreLocation TEST_SERVER_CERTIFICATE_TRUST = new KeystoreLocation(
            "certs/client_truststore.jks",
            "password",
            "JKS");

    /**
     * Information about a test keystore.
     *
     * @author Simon Taddiken
     */
    public static final class KeystoreLocation {
        private final String classpathLocation;
        private final String password;
        private final String type;

        private KeystoreLocation(String classpathLocation, String password, String type) {
            this.classpathLocation = classpathLocation;
            this.password = password;
            this.type = type;
        }

        /**
         * Location resolved from classpath.
         *
         * @return The location as URL.
         */
        public URL toURL() {
            return Resources.getResource(getClasspathLocation());
        }

        /**
         * Location that can be resolved using {@link ClassLoader#getResource(String)}.
         *
         * @return The classpath location.
         */
        public String getClasspathLocation() {
            return classpathLocation;
        }

        public String getLocation() {
            return toURL().toString();
        }

        public String getPassword() {
            return this.password;
        }

        public String getType() {
            return this.type;
        }

        /**
         * Materializes the keystore from its location.
         *
         * @return The keystore.
         */
        public KeyStore getKeystore() {
            try (InputStream in = toURL().openStream()) {
                final KeyStore keyStore = KeyStore.getInstance(getType());
                keyStore.load(in, getPassword().toCharArray());
                return keyStore;
            } catch (final KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
                throw new IllegalStateException(
                        "Could not read keystore from classpath location: " + classpathLocation);
            }
        }
    }
}
