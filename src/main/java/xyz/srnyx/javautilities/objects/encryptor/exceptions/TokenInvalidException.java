package xyz.srnyx.javautilities.objects.encryptor.exceptions;

import org.jetbrains.annotations.NotNull;


/**
 * Exception thrown when a token is invalid during {@link xyz.srnyx.javautilities.objects.encryptor.Encryptor#decrypt(String) decryption}
 */
public class TokenInvalidException extends DecryptionException {
    /**
     * Constructs a new {@link TokenInvalidException} with the specified detail message
     *
     * @param   message the detail message
     */
    public TokenInvalidException(@NotNull String message) {
        super(message);
    }
}
