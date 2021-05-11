package de.skuzzle.springboot.test.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.base.Preconditions;

final class WireMockHolder {
    private WireMockHolder() {
        // hidden
    }

    private static final ThreadLocal<WireMockServer> SERVER = new ThreadLocal<>();

    public static WireMockServer getServer() {
        final WireMockServer mockServer = SERVER.get();
        Preconditions.checkState(mockServer != null, "No server attached to current thread");
        return mockServer;
    }

    public static void setServer(WireMockServer server) {
        Preconditions.checkArgument(server != null, "server must not be null");
        Preconditions.checkState(SERVER.get() == null, "server already set");
        SERVER.set(server);
    }

    public static void clearServer() {
        Preconditions.checkState(SERVER.get() != null, "No server attached to current thread");
        SERVER.set(null);
    }
}
