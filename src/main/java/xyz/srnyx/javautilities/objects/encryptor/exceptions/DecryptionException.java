package xyz.srnyx.javautilities.objects.encryptor.exceptions;

import org.jetbrains.annotations.NotNull;


public class DecryptionException extends Exception {
    public DecryptionException(@NotNull String message) {
        super(message);
    }
}
