package io.target365.client;

import java.util.concurrent.Future;

public interface PingClient {
    /**
     * Performs a test to see if the service endpoint is responding.
     *
     * @return A simple string response 'pong'.
     */
    Future<String> getPing();
}
