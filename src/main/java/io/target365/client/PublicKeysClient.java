package io.target365.client;

import io.target365.dto.PublicKey;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.Future;

public interface PublicKeysClient {

    /**
     * Gets a server public key.
     *
     * @param keyName Public key name.
     * @return A server public key.
     */
    Future<PublicKey> getServerPublicKey(@NotNull final String keyName);

    /**
     * Gets all client public key.
     *
     * @return Lists all client public key.
     */
    Future<List<PublicKey>> getClientPublicKeys();

    /**
     * Gets a client public key.
     *
     * @param keyName Public key name.
     * @return A client public key.
     */
    Future<PublicKey> getClientPublicKey(@NotNull final String keyName);

    /**
     * Deletes a client public key.
     *
     * @param keyName Public key name.
     * @return Void
     */
    Future<Void> deleteClientPublicKey(@NotNull final String keyName);

}
