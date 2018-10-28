package io.target365.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ClientTest {

    private static final String PRIVATE_KEY_FILENAME = "private.key";
    private static final String PUBLIC_KEY_FILENAME = "public.key";

    public String getPrivateKeyAsString() throws Exception {
        final Path ecPrivateKeyPath = Paths.get(this.getClass().getClassLoader().getResource(PRIVATE_KEY_FILENAME).toURI());
        return Files.readAllLines(ecPrivateKeyPath, StandardCharsets.UTF_8).stream().reduce("", String::concat);
    }

    public String getPublicKeyAsString() throws Exception {
        final Path ecPublicKeyPath = Paths.get(this.getClass().getClassLoader().getResource(PUBLIC_KEY_FILENAME).toURI());
        return Files.readAllLines(ecPublicKeyPath, StandardCharsets.UTF_8).stream().reduce("", String::concat);
    }
}
