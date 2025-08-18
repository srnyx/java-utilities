package xyz.srnyx.javautilities.objects.encryptor.exceptions;

import org.jetbrains.annotations.NotNull;


public class TokenInvalidException extends DecryptionException {
    public TokenInvalidException(@NotNull String message) {
        super(message);
    }
}
