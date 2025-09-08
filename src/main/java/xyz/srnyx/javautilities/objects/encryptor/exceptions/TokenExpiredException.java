package xyz.srnyx.javautilities.objects.encryptor.exceptions;


/**
 * Exception thrown when a token has expired during {@link xyz.srnyx.javautilities.objects.encryptor.Encryptor#decrypt(String) decryption}
 */
public class TokenExpiredException extends DecryptionException {
    /**
     * Constructs a new {@link TokenExpiredException} with the default message
     */
    public TokenExpiredException() {
        super("Token has expired");
    }
}
