package xyz.srnyx.javautilities.objects.encryptor.exceptions;

import org.jetbrains.annotations.NotNull;


/**
 * Exception thrown when {@link xyz.srnyx.javautilities.objects.encryptor.Encryptor#decrypt(String) decryption} fails
 */
public class DecryptionException extends Exception {
    /**
     * Constructs a new {@link DecryptionException} with the specified detail message
     *
     * @param   message the detail message
     */
    public DecryptionException(@NotNull String message) {
        super(message);
    }
}
